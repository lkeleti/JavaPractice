package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_statement_line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private BankStatement bankStatement;

    private LocalDate date;

    private BigDecimal amount; // + be, - ki

    private String description;

    private String partnerNameRaw;

    private boolean processed = false;

    @OneToOne
    private MoneyTransaction moneyTransaction;
}