package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String description;

    @ManyToOne(optional = false)
    private GLAccount debitAccount;

    @ManyToOne(optional = false)
    private GLAccount creditAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private MoneyTransaction moneyTransaction;

    private LocalDateTime createdAt;
}