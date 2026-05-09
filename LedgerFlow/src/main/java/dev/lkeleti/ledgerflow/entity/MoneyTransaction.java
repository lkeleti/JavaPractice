package dev.lkeleti.ledgerflow.entity;

import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
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
@Table(name = "money_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoneyDirection direction;

    @ManyToOne(optional = false)
    private FinancialAccount financialAccount;

    private String description;

    private String externalId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean deleted = false;

    @OneToMany(
            mappedBy = "moneyTransaction",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Allocation> allocations = new ArrayList<>();
}
