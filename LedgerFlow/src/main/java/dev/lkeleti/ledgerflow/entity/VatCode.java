package dev.lkeleti.ledgerflow.entity;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VatCode {

    @Id
    @GeneratedValue
    private Long id;

    private String code; // pl: A27, A5, AM, TAM, EU, EXPORT

    private String name;

    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    private VatType type; // NORMAL, EXEMPT, REVERSE, OUT_OF_SCOPE
}