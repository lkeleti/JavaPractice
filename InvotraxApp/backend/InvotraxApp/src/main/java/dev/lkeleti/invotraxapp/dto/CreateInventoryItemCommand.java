package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateInventoryItemCommand {
    private Long productId;
    private int quantity;
    private BigDecimal netPurchasePrice;
    private BigDecimal grossPurchasePrice;
    private BigDecimal netSellingPrice;
    private BigDecimal grossSellingPrice;
    private Integer warrantyPeriodMonths;
}
