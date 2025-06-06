package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateInventoryItemCommand {
    private Long productId;
    private int quantity;
    private BigDecimal netPurchasePrice;
    private BigDecimal grossPurchasePrice;
    private BigDecimal netSellingPrice;
    private BigDecimal grossSellingPrice;
    private Integer warrantyPeriodMonths;
    private List<String> serialNumbers;
}
