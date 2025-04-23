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

    @Column(name = "net_purchase_price")
    private BigDecimal netPurchasePrice;

    @Column(name = "gross_purchase_price")
    private BigDecimal grossPurchasePrice;

    @Column(name = "net_selling_price")
    private BigDecimal netSellingPrice;

    @Column(name = "gross_selling_price")
    private BigDecimal grossSellingPrice;

    @Column(name = "warranty_period_months")
    private Integer warrantyPeriodMonths;

    @Column(name = "serial_number_required")
    private boolean serialNumberRequired;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Barcode> barcodes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SerialNumber> serialNumbers;

    @ManyToOne
    @JoinColumn(name = "vat_rate_id")
    private VatRate vatRate;

    private boolean deleted;

}
