package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_vat_summary")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceVatSummary {
    //ÁFA bontás
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Invoice invoice;

    @ManyToOne
    private VatCode vatCode;

    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal grossAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;
}