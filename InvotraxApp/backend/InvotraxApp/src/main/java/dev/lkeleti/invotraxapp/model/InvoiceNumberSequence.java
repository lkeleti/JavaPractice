package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoice_number_sequence")
public class InvoiceNumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_type_id", nullable = false)
    private InvoiceType invoiceType;

    @Column(name = "invoice_prefix", nullable = false)
    private String invoicePrefix;

    @Column(name = "last_number", nullable = false)
    private int lastNumber;
}