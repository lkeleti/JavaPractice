package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private SellerCompanyProfile seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Partner buyer;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "fulfillment_at")
    private LocalDate fulfillmentAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "invoice_type_id")
    private InvoiceType invoiceType;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "net_total")
    private BigDecimal netTotal;

    @Column(name = "gross_total")
    private BigDecimal grossTotal;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "pdf_password")
    private String pdfPassword;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items;
}
