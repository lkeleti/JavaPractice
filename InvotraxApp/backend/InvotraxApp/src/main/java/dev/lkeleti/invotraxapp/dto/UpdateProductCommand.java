package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    private String name;
    private String sku;
    private String description;
    private String unit;
    private Long categoryId;
    private Long manufacturerId;
    private BigDecimal netPurchasePrice;
    private BigDecimal grossPurchasePrice;
    private BigDecimal netSellingPrice;
    private BigDecimal grossSellingPrice;
    private Integer warrantyPeriodMonths;
    private boolean serialNumberRequired;
    private int stockQuantity;
    private Long vatRateId;
    private boolean deleted;
}
