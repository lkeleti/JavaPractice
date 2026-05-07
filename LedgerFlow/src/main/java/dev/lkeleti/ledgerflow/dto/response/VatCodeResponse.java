package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VatCodeResponse {

    private Long id;

    private String code;

    private String name;

    private BigDecimal rate;

    private VatType type;

    private boolean deductible;

    private boolean active;
}