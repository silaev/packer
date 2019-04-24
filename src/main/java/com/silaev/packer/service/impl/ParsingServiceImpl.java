package com.silaev.packer.service.impl;

import com.silaev.packer.di.AbstractService;
import com.silaev.packer.di.Services;
import com.silaev.packer.exception.APIException;
import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.ParsingService;
import com.silaev.packer.service.PropertyService;
import com.silaev.packer.util.FilenameUtils;
import com.silaev.packer.util.LambdaExceptionUtil;
import com.silaev.packer.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serves the purpose of parsing a text file.
 */
public class ParsingServiceImpl extends AbstractService implements ParsingService {

    //space, currency symbol, (, )
    public static final String CURRENCY_SYMBOLS = "\\p{Sc}";
    public static final String SPACE_SYMBOL = "\\s";
    public static final String ONE_OR_MORE_SPACES = SPACE_SYMBOL + "+";
    public static final String GROUP_SYMBOLS = ":|\\(|\\)";
    public static final String TXT = "txt";
    public static final Comparator<Item> ITEM_COMPARATOR = Comparator
        .comparing(Item::getCost).reversed()
        .thenComparing(Item::getWeight)
        .thenComparing(Item::getIndexNumber);


    private final PropertyService propertyService;

    public ParsingServiceImpl(Services services, PropertyService propertyService) {
        super(services);
        this.propertyService = propertyService;
    }

    @Override
    public List<ItemPackage> parseFile(String filePath) throws APIException {
        if (StringUtils.isBlank(filePath)) {
            throw new APIException("The filePath parameter is blank.");
        }

        Path path;
        try {
            path = Paths.get(filePath);
        } catch (InvalidPathException ipe) {
            throw new APIException(ipe.getMessage(), ipe);
        }

        final String extension = FilenameUtils.getExtension(filePath);
        if (!TXT.equals(extension)) {
            throw new APIException(
                String.format("The extension: %s is not currently supported.", extension));
        }

        try (Stream<String> rowStream = Files.lines(path)) {
            final List<ItemPackage> itemPackages = rowStream
                .filter(StringUtils::isNotBlank)
                .map(LambdaExceptionUtil.rethrowFunction(this::parseItemPackageRow))
                .collect(Collectors.toList());

            if (itemPackages.isEmpty()) {
                throw new APIException(String.format("A file: %s is empty.", filePath));
            }

            return itemPackages;

        } catch (IOException e) {
            throw new APIException(e.getMessage(), e);
        }

    }

    //@SneakyThrows eliminates throws APIException
    private ItemPackage parseItemPackageRow(String itemPackageRow) throws APIException {
        final String itemPackageRowCleaned = itemPackageRow
            .replaceAll(GROUP_SYMBOLS, " ")
            .replaceAll(CURRENCY_SYMBOLS, "");
        final String[] itemPackageRowGroups = itemPackageRowCleaned.split(ONE_OR_MORE_SPACES);

        final int rowGroupsLength = itemPackageRowGroups.length;

        if (rowGroupsLength < 2) {
            throw new APIException("A package in a file should have at least weight limit and one item.");
        }

        final String weightLimitFile = itemPackageRowGroups[0];
        if (StringUtils.isBlank(weightLimitFile)) {
            throw new APIException("A file has incorrect structure");
        }

        BigDecimal weightLimit;
        try {
            weightLimit = new BigDecimal(weightLimitFile);
        } catch (NumberFormatException nfe) {
            throw new APIException(nfe.getMessage(), nfe);
        }
        if (weightLimit.scale() > propertyService.getWeightScale()) {
            throw new APIException(
                String.format(
                    "A package weight: %s exceeds the max scale: %d",
                    weightLimit.toPlainString(),
                    propertyService.getWeightScale()));
        }

        if (weightLimit.compareTo(propertyService.getMaxWeight()) > 0) {
            throw new APIException(
                String.format("A weight limit: %s is more than the max acceptable weight: %s",
                    weightLimit.toPlainString(), propertyService.getMaxWeight().toPlainString()));
        }

        final int itemSize = rowGroupsLength - 1; // 1 is for weightLimit
        if (itemSize > propertyService.getItemLimit()) {
            throw new APIException(
                String.format("A package with weight: %s has items: %d, but the item limit is: %d",
                    weightLimit.toPlainString(), itemSize, propertyService.getItemLimit()));
        }

        Item[] items = Stream.of(itemPackageRowGroups)
            .skip(1)//1 is for weightLimit
            .map(LambdaExceptionUtil.rethrowFunction(s -> parseItemRow(s, weightLimit)))
            .filter(i -> i.getWeight().compareTo(weightLimit) <= 0)
            .sorted(ITEM_COMPARATOR)
            .toArray(Item[]::new);

        return ItemPackage.builder()
            .weightLimit(weightLimit)
            .items(items)
            .build();
    }

    //@SneakyThrows eliminates throws APIException
    private Item parseItemRow(String itemRow, BigDecimal weightLimit) throws APIException {
        final String[] itemRowGroups = itemRow.split(",");

        if (itemRowGroups.length != 3) {
            throw new APIException(
                String.format("A package with weight: %s has incorrect item structure. " +
                        "An item is supposed to have index number, weight and cost",
                    weightLimit.toPlainString()));
        }

        Integer indexNumber;
        BigDecimal weight;
        BigDecimal cost;
        try {
            indexNumber = Integer.valueOf(itemRowGroups[0]);
            weight = new BigDecimal(itemRowGroups[1]);
            cost = new BigDecimal(itemRowGroups[2]);
        } catch (NumberFormatException nfe) {
            throw new APIException(nfe.getMessage(), nfe);
        }

        if ((weight.compareTo(propertyService.getMaxWeight()) > 0) || (cost.compareTo(propertyService.getMaxCost()) > 0)) {
            throw new APIException(
                String.format("A package with weight: %s has an item with weight: %s and cost: %s, " +
                        "but the max weight and cost are: %s %s respectively",
                    weightLimit.toPlainString(), weight.toPlainString(), cost.toPlainString(),
                    propertyService.getMaxWeight().toPlainString(), propertyService.getMaxCost().toPlainString()));
        }

        if (weight.scale() > propertyService.getWeightScale()) {
            throw new APIException(
                String.format(
                    "A package with weight: %s has an item with weight: %s that exceeds the max scale: %d",
                    weightLimit.toPlainString(),
                    weight.toPlainString(),
                    propertyService.getWeightScale()));
        }

        return Item.builder()
            .indexNumber(indexNumber)
            .weight(weight)
            .cost(cost)
            .build();
    }
}
