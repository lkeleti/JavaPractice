package dev.lkeleti.ledgerflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentMethodResponse {

    @Schema(description = "A fizetési mód azonosítója", example = "5")
    private Long id;

    @Schema(description = "A fizetési mód neve", example = "Átutalás")
    private String name;

    @Schema(description = "A fizetési mód kódja", example = "TRANSFER")
    private String code;

    @Schema(description = "Generál-e pénzmozgást", example = "true")
    private boolean financial;

    @Schema(description = "Készpénzes fizetés-e", example = "false")
    private boolean cash;

    @Schema(description = "Aktív-e a fizetési mód", example = "true")
    private boolean active;

    @Schema(description = "Létrehozás ideje", example = "2025-05-01T10:15:30")
    private LocalDateTime createdAt;
}
