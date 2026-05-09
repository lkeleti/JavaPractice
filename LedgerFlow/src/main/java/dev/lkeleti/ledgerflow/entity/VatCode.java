package dev.lkeleti.ledgerflow.entity;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vat_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VatCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VatType type;

    private boolean deductible;

    private boolean active = true;

    private boolean deleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
