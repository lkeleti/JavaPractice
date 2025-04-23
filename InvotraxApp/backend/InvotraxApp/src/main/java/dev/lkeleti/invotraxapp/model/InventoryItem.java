package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

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
}
