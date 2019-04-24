package com.silaev.packer.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Builder
@EqualsAndHashCode(of = {"weightLimit", "items"})
public class ItemPackage {
    private BigDecimal weightLimit;
    private Item[] items;
}
