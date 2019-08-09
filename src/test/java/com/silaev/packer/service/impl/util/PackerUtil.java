package com.silaev.packer.service.impl.util;

import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.impl.ParsingServiceImpl;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class PackerUtil {
    private PackerUtil() {

    }

    public static Item mockItem(int indexNumber, String weight, String cost) {
        return Item.builder()
            .indexNumber(indexNumber)
            .weight(new BigDecimal(weight))
            .cost(new BigDecimal(cost))
            .build();
    }

    public static ItemPackage mockItemPackage(String weightLimit, Item[] items) {

        Item[] itemsSorted = Stream.of(items).sorted(ParsingServiceImpl.ITEM_COMPARATOR).toArray(Item[]::new);
        return ItemPackage.builder()
            .weightLimit(new BigDecimal(weightLimit))
            .items(itemsSorted)
            .build();
    }

    public static class StringToItemsArray extends SimpleArgumentConverter {
        @Override
        protected Item[] convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            //1, 53.38, 45; 3, 78.48, 3
            String[] groups = ((String) source).split("\\s*;\\s*");
            return Stream.of(groups)
                .map(this::mapItem)
                .toArray(Item[]::new);


        }

        private Item mapItem(String itemString) {
            String[] item = itemString.split("\\s*,\\s*");
            return Item.builder()
                .indexNumber(new Integer(item[0]))
                .weight(new BigDecimal(item[1]))
                .cost(new BigDecimal(item[2]))
                .build();
        }
    }

    public static class CSVtoArray extends SimpleArgumentConverter {
        @Override
        protected String[] convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            String s = (String) source;
            return s.split("\\s*,\\s*");
        }
    }
}
