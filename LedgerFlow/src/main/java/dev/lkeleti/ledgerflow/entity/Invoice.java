package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private LocalDate issueDate;
    private LocalDate fulfillmentDate;
    private LocalDate dueDate;

    @ManyToOne
    private Partner partner; // nyugta esetén null

    @Enumerated(EnumType.STRING)
    private InvoiceType type; // VEVŐ, SZÁLLÍTÓ

    @Enumerated(EnumType.STRING)
    private InvoiceCategory category; // SZÁMLA, NYUGTA

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private BigDecimal netTotal;
    private BigDecimal grossTotal;

    private boolean electronicInvoice;

    private String scannedFilePath;
    private String scannedFileName;

    private String electronicFilePath;
    private String electronicFileName;

    private String hashFilePath;
    private String hashFileName;

    @Enumerated(EnumType.STRING)
    private InvoiceNature nature; // NORMAL, STORNO, HELYESBITO, JOVAIRAS

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceVatSummary> vatSummaries = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}