package com.silaev.packer.service.impl;

import com.silaev.packer.Packer;
import com.silaev.packer.di.ServiceFactory;
import com.silaev.packer.di.Services;
import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.CollectingService;
import com.silaev.packer.service.impl.util.PackerUtil;
import com.silaev.packer.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CollectingServiceImplTest {
    private CollectingService collectingService;

    @BeforeEach
    void setUp() {
        final Services services = ServiceFactory.createServices();
        collectingService = services.getCollectingService();
    }

    @ParameterizedTest(name = "{index}: weightLimit: {0}, items: {1}, expectedItemIndexes: {2}")
    @CsvSource(value = {
        "81@ 1,53.38,45; 3,78.48,3; 4,72.30,76; 5,30.18,9; 6,46.34,48@ 4",
        "8@ 1,15.3,34@ " + Packer.NONE_ITEMS,
        "75@ 2,14.55,74; 3,3.98,16; 4,26.24,55; 5,63.69,52; 7,60.02,74@ 2,7",
        "56@ 2,33.80,40; 3,43.15,10; 4,37.97,16; 5,46.81,36; 6,48.77,79; 8,19.36,79; 9,6.76,64@ 8,9",
        "81@ 1,80.38,45; 2,35.62,40; 3,35.48,40@ 2,3",
        "101@ 1,50,30.33; 2,50.25,60.23; 3,50.25,40.77@ 2,3",
        "7.05@ 1,0.15,1; 2,0.28,10; 3,5.35,12; 4,6.25,8; 5,3.25,10; 6,0.55,2@ 6,3,1,2"
    }, delimiter = '@')
    void shouldGetOptimalItems(String weightLimit,
                               @ConvertWith(PackerUtil.StringToItemsArray.class) Item[] items,
                               @ConvertWith(PackerUtil.CSVtoArray.class) String[] expectedItemIndexes) {
        //GIVEN
        ItemPackage itemPackage = PackerUtil.mockItemPackage(weightLimit, items);
        Set<Integer> expectedItemsSet = Stream.of(expectedItemIndexes)
            .filter(s -> !Packer.NONE_ITEMS.equals(s))
            .map(Integer::valueOf)
            .collect(Collectors.toSet());

        List<Item> itemListExpected = Stream.of(items)
            .filter(i -> expectedItemsSet.contains(i.getIndexNumber()))
            .collect(Collectors.toList());

        //WHEN
        List<Item> optimalItems = collectingService.getOptimalItems(itemPackage);

        /*
        Optional<BigDecimal> totalSum = optimalItems.stream()
            .map(Item::getCost)
            .reduce(BigDecimal::add);
        totalSum.ifPresent(System.out::println);
        */

        //THEN
        Assertions.assertTrue(CollectionUtils.isEqualCollection(itemListExpected, optimalItems));
    }
}
