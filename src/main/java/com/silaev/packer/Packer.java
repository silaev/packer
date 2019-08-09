package com.silaev.packer;

import com.silaev.packer.di.ServiceFactory;
import com.silaev.packer.di.Services;
import com.silaev.packer.exception.APIException;
import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.CollectingService;
import com.silaev.packer.service.ParsingService;
import com.silaev.packer.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Packs a package.
 */
public class Packer {
    public static final String NONE_ITEMS = "-";

    public static void main(String[] args) throws APIException {

        if (args.length == 0 || StringUtils.isBlank(args[0])) {
            throw new IllegalArgumentException("Please, provide a filePath.");
        }

        System.out.println(pack(args[0]));
    }

    /**
     * Gets a string representing item indexes for optimal items.
     * If there is no items, then returns {@link #NONE_ITEMS}
     *
     * @param filePath
     * @return
     * @throws APIException
     */
    public static String pack(String filePath) throws APIException {
        final Services services = ServiceFactory.createServices();
        final ParsingService parsingService = services.getParsingService();
        final List<ItemPackage> itemPackages = parsingService.parseFile(filePath);

        final CollectingService collectingService = services.getCollectingService();
        final StringBuilder sb = new StringBuilder();
        itemPackages.forEach(e -> {
            final String items = collectingService.getOptimalItems(e)
                .stream()
                .map(Item::getIndexNumber)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            sb.append((StringUtils.isBlank(items) ? NONE_ITEMS : items));
            sb.append(System.lineSeparator());
        });
        return sb.toString();
    }
}
