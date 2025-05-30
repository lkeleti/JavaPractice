package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
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
    private int stockQuantity;
    private boolean serialNumberRequired;
    private Long vatRateId;
    private Long productTypeId;
    private List<BarcodeDto> barcodes;
    private List<SerialNumberDto> serialNumbers;
}