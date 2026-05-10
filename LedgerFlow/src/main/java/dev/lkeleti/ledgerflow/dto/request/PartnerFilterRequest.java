package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerFilterRequest {

    @Schema(description = "Név szerinti szűrés (LIKE)", example = "Kft")
    private String name;

    @Schema(description = "Adószám szerinti szűrés", example = "12345678-1-42")
    private String taxNumber;

    @Schema(
            description = "Törölt státusz szűrése. true = csak törölt, false = csak aktív, null = mind",
            example = "false"
    )
    private Boolean deleted = false;   // ← ALAPÉRTELMEZETT: csak aktív

    @Schema(
            description = "Magánszemély szűrése. true = csak magánszemély, false = csak cég, null = mind",
            example = "null"
    )
    private Boolean privatePerson = null; // ← ALAPÉRTELMEZETT: mind
}
