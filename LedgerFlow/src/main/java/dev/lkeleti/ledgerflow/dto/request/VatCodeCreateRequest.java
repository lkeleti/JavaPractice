package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VatCodeCreateRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private VatType type;

    private boolean deductible;

    private boolean active = true;
}