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

    //Az alábbi mezők azért kellenek, hogy a számla kiállításának pillanatában érvényes adatokat le tudjam tárolni!!
    @Column(name = "seller_name")
    private String sellerName;

    @Column(name = "seller_tax_number")
    private String sellerTaxNumber;

    @Column(name = "seller_address")
    private String sellerAddress;

    @Column(name = "seller_head_office_address")
    private String sellerHeadOfficeAddress;

    @Column(name = "seller_default_branch_address")
    private String sellerDefaultBranchAddress;

    @Column(name = "seller_company_reg_number")
    private String sellerCompanyRegNumber;

    @Column(name = "seller_bank_details")
    private String sellerBankDetails;

    @Column(name = "seller_iban")
    private String sellerIban;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_tax_number")
    private String buyerTaxNumber;

    @Column(name = "buyer_address")
    private String buyerAddress;

    @Column(name = "invoice_type_name")
    private String invoiceTypeName;

    @Column(name = "payment_method_name")
    private String paymentMethodName;
    ///////////////////////

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
