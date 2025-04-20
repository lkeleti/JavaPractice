package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String sku;
    private String description;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(name = "net_price")
    private BigDecimal netPrice;

    @Column(name = "gross_price")
    private BigDecimal grossPrice;

    @Column(name = "warranty_period_months")
    private Integer warrantyPeriodMonths;

    @Column(name = "serial_number_required")
    private boolean serialNumberRequired;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Barcode> barcodes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SerialNumber> serialNumbers;

    @ManyToOne
    @JoinColumn(name = "vat_rate_id")
    private VatRate vatRate;

    private boolean deleted;

    public Product(String name, String sku, String description, ProductCategory category, Manufacturer manufacturer, BigDecimal netPrice, BigDecimal grossPrice, Integer warrantyPeriodMonths, boolean serialNumberRequired, List<Barcode> barcodes, List<SerialNumber> serialNumbers, int stockQuantity, VatRate vatRate) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.manufacturer = manufacturer;
        this.netPrice = netPrice;
        this.grossPrice = grossPrice;
        this.warrantyPeriodMonths = warrantyPeriodMonths;
        this.serialNumberRequired = serialNumberRequired;
        this.barcodes = barcodes;
        this.serialNumbers = serialNumbers;
        this.vatRate = vatRate;
        this.stockQuantity = stockQuantity;
        deleted = false;
    }
}
