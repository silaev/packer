package com.silaev.packer.service.impl;

import com.silaev.packer.di.AbstractService;
import com.silaev.packer.di.Services;
import com.silaev.packer.model.Item;
import com.silaev.packer.model.ItemPackage;
import com.silaev.packer.service.CollectingService;
import com.silaev.packer.service.PropertyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Computes an optimal package with a set of items.
 */
public class CollectingServiceImpl extends AbstractService implements CollectingService {
    private final PropertyService propertyService;

    public CollectingServiceImpl(Services services, PropertyService propertyService) {
        super(services);
        this.propertyService = propertyService;
    }

    /**
     * Solves the Knapsack problem via the dynamic programming approach
     * This solution will therefore run in O(n*weightScale W) time and O(n*weightScale W) space.
     *
     * @param itemPackage
     * @return
     */
    @Override
    public List<Item> getOptimalItems(ItemPackage itemPackage) {
        Objects.requireNonNull(itemPackage);

        Item[] items = itemPackage.getItems();
        int capacity = toIntIndex(itemPackage.getWeightLimit());

        int itemsNumber = items.length;
        // a matrix to store the max value at each n-th item
        BigDecimal[][] matrix = new BigDecimal[itemsNumber + 1][capacity + 1];

        // first line is initialized to 0
        for (int i = 0; i <= capacity; i++)
            matrix[0][i] = BigDecimal.ZERO;

        // we iterate on items
        for (int i = 1; i <= itemsNumber; i++) {
            // we iterate on each capacity
            for (int j = 0; j <= capacity; j++) {
                if (toIntIndex(items[i - 1].getWeight()) > j)
                    matrix[i][j] = matrix[i - 1][j];
                else
                    // we maximize value at this rank in the matrix
                    matrix[i][j] = matrix[i - 1][j].max(
                        matrix[i - 1][j - toIntIndex(items[i - 1].getWeight())]
                            .add(items[i - 1].getCost()));
            }
        }

        BigDecimal totalCost = matrix[itemsNumber][capacity];
        int totalWeight = capacity;
        final List<Item> optimalItems = new ArrayList<>();

        for (int i = itemsNumber; i > 0 && totalCost.compareTo(BigDecimal.ZERO) > 0; i--) {
            if (!totalCost.equals(matrix[i - 1][totalWeight])) {
                optimalItems.add(items[i - 1]);
                // we remove items value and weight
                totalCost = totalCost.subtract(items[i - 1].getCost());
                totalWeight -= toIntIndex(items[i - 1].getWeight());
            }
        }

        return optimalItems;
    }

    private int toIntIndex(BigDecimal decimal) {
        return decimal.multiply(
            new BigDecimal(propertyService.getWeightScale())
        ).intValue();
    }
}
