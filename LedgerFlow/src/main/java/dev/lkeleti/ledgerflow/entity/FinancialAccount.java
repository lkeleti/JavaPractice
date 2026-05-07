package dev.lkeleti.ledgerflow.entity;

import dev.lkeleti.ledgerflow.entity.enums.FinancialAccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    // pl: OTP főszámla
    @Column(nullable = false)
    private String name;

    // bankszámla / pénztár / cash / stb
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialAccountType type;

    // bankszámlaszám
    private String accountNumber;

    private String iban;
    private String swift;

    // kapcsolt főkönyvi számla (pl. 384)
    @ManyToOne(optional = false)
    private GLAccount glAccount;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}