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
@Table(name = "invoice_item")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Az alábbi mezők azért kellenek, hogy a számla kiállításának pillanatában érvényes állapotot le tudjam tárolni
    @Column(name = "product_name")
    private String productName;

    @Column(name = "warranty_period_months_at_sale")
    private Integer warrantyPeriodMonthsAtSale;
    ///////////


    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "net_amount")
    private BigDecimal netAmount;

    @Column(name = "gross_amount")
    private BigDecimal grossAmount;

    @ManyToOne
    @JoinColumn(name = "vat_rate_id")
    private VatRate vatRate;

}
