package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VatCodeUpdateRequest {

    @Schema(description = "ÁFA kód", example = "27%")
    @NotBlank(message = "A kód megadása kötelező")
    private String code;

    @Schema(description = "ÁFA kód megnevezése", example = "Belföldi 27%")
    @NotBlank(message = "A név megadása kötelező")
    private String name;

    @Schema(description = "ÁFA kulcs értéke", example = "27.00")
    @NotNull(message = "Az ÁFA kulcs megadása kötelező")
    private BigDecimal rate;

    @Schema(description = "ÁFA típus", example = "STANDARD")
    @NotNull(message = "Az ÁFA típus megadása kötelező")
    private VatType type;

    @Schema(description = "Levonható-e", example = "true")
    private boolean deductible;

    @Schema(description = "Aktív-e", example = "true")
    private boolean active;
}
