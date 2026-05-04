package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "financial_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private FinancialAccountType type; // BANK, PENZTAR

    @ManyToOne(optional = false)
    private GLAccount glAccount;
}