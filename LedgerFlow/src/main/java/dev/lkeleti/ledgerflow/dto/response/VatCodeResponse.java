package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.VatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VatCodeResponse {

    @Schema(description = "ÁFA kód azonosítója", example = "5")
    private Long id;

    @Schema(description = "ÁFA kód", example = "27%")
    private String code;

    @Schema(description = "ÁFA kód megnevezése", example = "Belföldi 27%")
    private String name;

    @Schema(description = "ÁFA kulcs értéke", example = "27.00")
    private BigDecimal rate;

    @Schema(description = "ÁFA típus", example = "STANDARD")
    private VatType type;

    @Schema(description = "Levonható-e", example = "true")
    private boolean deductible;

    @Schema(description = "Aktív-e", example = "true")
    private boolean active;

    @Schema(description = "Törölt-e", example = "false")
    private boolean deleted;

    @Schema(description = "Létrehozás ideje", example = "2025-05-01T10:15:30")
    private LocalDateTime createdAt;
}
