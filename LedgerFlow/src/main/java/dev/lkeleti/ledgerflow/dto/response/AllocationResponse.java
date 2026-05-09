package dev.lkeleti.ledgerflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AllocationResponse {

    @Schema(description = "Allokáció azonosítója", example = "5")
    private Long id;

    @Schema(description = "A számla azonosítója", example = "12")
    private Long invoiceId;

    @Schema(description = "A kiegyenlített összeg", example = "150000")
    private BigDecimal amount;

    @Schema(description = "Törölt-e az allokáció", example = "false")
    private boolean deleted;

    @Schema(description = "Létrehozás ideje", example = "2025-05-01T10:15:30")
    private LocalDateTime createdAt;
}
