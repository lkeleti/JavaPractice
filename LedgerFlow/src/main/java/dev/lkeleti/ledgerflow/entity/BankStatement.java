package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bank_statement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate statementDate;

    @ManyToOne(optional = false)
    private FinancialAccount account;

    @OneToMany(mappedBy = "bankStatement", cascade = CascadeType.ALL)
    private List<BankStatementLine> lines = new ArrayList<>();
}