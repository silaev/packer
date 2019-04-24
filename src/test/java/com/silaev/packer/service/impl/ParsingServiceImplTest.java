package com.silaev.packer.service.impl;

import com.silaev.packer.di.ServiceFactory;
import com.silaev.packer.di.Services;
import com.silaev.packer.exception.APIException;
import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.ParsingService;
import com.silaev.packer.service.impl.util.PackerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ParsingServiceImplTest {
    private ParsingService parsingService;

    @BeforeEach
    void setUp() {
        final Services services = ServiceFactory.createServices();
        parsingService = services.getParsingService();
    }

    @Test
    void shouldParseFile() throws APIException {
        //GIVEN
        String filePath = "src/test/resources/input_files/correct/input.txt";

        Item[] items1 = {
            PackerUtil.mockItem(1, "53.38", "45"),
            PackerUtil.mockItem(3, "78.48", "3"),
            //items1.add(mockItem(2, "88.62", "98"));
            PackerUtil.mockItem(4, "72.30", "76"),
            PackerUtil.mockItem(5, "30.18", "9"),
            PackerUtil.mockItem(6, "46.34", "48")

        };
        ItemPackage itemPackage1 = PackerUtil.mockItemPackage("81", items1);

        Item[] items2 = {
            //items2.add(mockItem(1, "15.3", "34"));
        };
        ItemPackage itemPackage2 = PackerUtil.mockItemPackage("8", items2);

        Item[] items3 = {
            //items3.add(mockItem(1, "85.31", "29"));
            PackerUtil.mockItem(2, "14.55", "74"),
            PackerUtil.mockItem(3, "3.98", "16"),
            PackerUtil.mockItem(4, "26.24", "55"),
            PackerUtil.mockItem(5, "63.69", "52"),
            //items3.add(mockItem(6, "76.25", "75"));
            PackerUtil.mockItem(7, "60.02", "74"),
            //items3.add(mockItem(8, "93.18", "35"));
            //items3.add(mockItem(9, "89.95", "78"));
        };
        ItemPackage itemPackage3 = PackerUtil.mockItemPackage("75", items3);

        Item[] items4 = {
            //items4.add(mockItem(1, "90.72", "13"));
            PackerUtil.mockItem(2, "33.80", "40"),
            PackerUtil.mockItem(3, "43.15", "10"),
            PackerUtil.mockItem(4, "37.97", "16"),
            PackerUtil.mockItem(5, "46.81", "36"),
            PackerUtil.mockItem(6, "48.77", "79"),
            //items4.add(mockItem(7, "81.80", "45"));
            PackerUtil.mockItem(8, "19.36", "79"),
            PackerUtil.mockItem(9, "6.76", "64")
        };
        ItemPackage itemPackage4 = PackerUtil.mockItemPackage("56", items4);

        List<ItemPackage> itemPackagesExpected =
            Arrays.asList(itemPackage1, itemPackage2, itemPackage3, itemPackage4);

        //WHEN
        List<ItemPackage> itemPackagesActual = parsingService.parseFile(filePath);
        Map<BigDecimal, Item[]> itemPackagesActualIndex = itemPackagesActual.stream()
            .collect(Collectors.toMap(
                ItemPackage::getWeightLimit,
                ItemPackage::getItems));

        //THEN
        assertEquals(itemPackagesExpected.size(), itemPackagesActual.size());
        itemPackagesExpected.forEach(i -> {
            Item[] itemsActual = itemPackagesActualIndex.get(i.getWeightLimit());
            assertArrayEquals(i.getItems(), itemsActual);
        });
    }

    @ParameterizedTest(name = "{index}: filePath: {0}")
    @ValueSource(strings = {
        //shouldNotParseFileBecauseOfFilePathOrIncorrectStructure
        "",
        "  ",
        "*.txt",
        "src/test/resources/input_files/incorrect/pdf_format.pdf",
        "src/test/resources/input_files/incorrect/broken_structure.txt",
        "src/test/resources/input_files/incorrect/broken_item.txt",
        "src/test/resources/input_files/incorrect/empty_item.txt",
        "src/test/resources/input_files/incorrect/incorrect_item_indexNumber.txt",
        "src/test/resources/input_files/incorrect/incorrect_item_weight.txt",
        "src/test/resources/input_files/incorrect/incorrect_item_cost.txt",
        "src/test/resources/input_files/incorrect/correct_package_weight.txt",

        //shouldNotParseFileBecauseOfConstraintsViolated
        "src/test/resources/exceeding_package_weight.txt",
        "src/test/resources/exceeding_item_weight_limit.txt",
        "src/test/resources/exceeding_items_limit.txt",
        "src/test/resources/exceeding_item_cost_limit.txt",
        "src/test/resources/exceeding_item_weight_scale.txt",
        "src/test/resources/exceeding_package_weight_scale.txt"
    })
    void shouldNotParseFile(String filePath) throws APIException {
        //GIVEN
        //filePath

        //WHEN
        Executable executable = () -> parsingService.parseFile(filePath);

        //THEN
        assertThrows(APIException.class, executable);
    }
}
