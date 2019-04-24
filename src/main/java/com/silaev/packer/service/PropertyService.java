package com.silaev.packer.service;

import java.math.BigDecimal;

public interface PropertyService {
    BigDecimal getMaxWeight();

    BigDecimal getMaxCost();

    int getItemLimit();

    int getWeightScale();
}
