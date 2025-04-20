package dev.lkeleti.invotraxapp.dto;

import dev.lkeleti.invotraxapp.model.ProductType;
import dev.lkeleti.invotraxapp.model.VatRate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private ProductCategoryDto category;
    private ManufacturerDto manufacturer;
    private BigDecimal netPrice;
    private BigDecimal grossPrice;
    private Integer warrantyPeriodMonths;
    private boolean serialNumberRequired;
    private int stockQuantity;
    private ProductType productType;
    private List<BarcodeDto> barcodes;
    private List<SerialNumberDto> serialNumbers;
    private VatRate vatRate;
    private boolean deleted;
}
