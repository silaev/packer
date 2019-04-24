package com.silaev.packer.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"indexNumber", "weight", "cost"})
@Builder
public class Item {
    private Integer indexNumber;
    private BigDecimal weight;
    private BigDecimal cost;
}
