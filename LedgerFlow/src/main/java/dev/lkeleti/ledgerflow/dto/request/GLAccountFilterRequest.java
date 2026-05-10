package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLAccountFilterRequest {

    @Schema(description = "Számlaszám szerinti szűrés (LIKE)", example = "31")
    private String number;

    @Schema(description = "Megnevezés szerinti szűrés (LIKE)", example = "vevő")
    private String name;

    @Schema(description = "Főkönyvi számla típusa", example = "ASSET")
    private GLAccountType type;

    @Schema(
            description = "Aktív státusz szűrése. false = csak inaktív, true = csak aktív, null = mind",
            example = "true"
    )
    private Boolean active = true;

    @Schema(description = "ÁFA-hoz kapcsolódik-e", example = "true")
    private Boolean vatRelated;

    @Schema(description = "Partner kartonhoz kapcsolódik-e (vevő)", example = "true")
    private Boolean customerRelated;

    @Schema(description = "Partner kartonhoz kapcsolódik-e (szállító)", example = "true")
    private Boolean supplierRelated;

    @Schema(description = "Könyvelhető-e közvetlenül", example = "true")
    private Boolean bookable;
}
