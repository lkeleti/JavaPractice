package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounting_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountingConfig {

    @Id
    private Long id = 1L; // singleton

    @ManyToOne
    private GLAccount revenueAccount;        // pl. 91

    @ManyToOne
    private GLAccount expenseAccount;        // pl. 51

    @ManyToOne
    private GLAccount vatPayableAccount;     // 467

    @ManyToOne
    private GLAccount vatReceivableAccount;  // 466

    @ManyToOne
    private GLAccount vat27Account;

    @ManyToOne
    private GLAccount vat5Account;

    @ManyToOne
    private GLAccount vat0Account;
}