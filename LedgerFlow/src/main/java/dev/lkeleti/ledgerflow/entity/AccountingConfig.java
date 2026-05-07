package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounting_config")
@Getter
@Setter
@NoArgsConstructor
public class AccountingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 91
    @ManyToOne
    private GLAccount revenueAccount;

    // 51
    @ManyToOne
    private GLAccount expenseAccount;

    // 467
    @ManyToOne
    private GLAccount vatPayableAccount;

    // 466
    @ManyToOne
    private GLAccount vatReceivableAccount;
}