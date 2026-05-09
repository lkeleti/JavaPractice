package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLAccountResponse {

    @Schema(description = "A főkönyvi számla azonosítója", example = "12")
    private Long id;

    @Schema(description = "A főkönyvi számla száma", example = "311")
    private String number;

    @Schema(description = "A főkönyvi számla megnevezése", example = "Vevők")
    private String name;

    @Schema(description = "A számla típusa", example = "ASSET")
    private GLAccountType type;

    @Schema(description = "ÁFA-hoz kapcsolódik-e", example = "false")
    private boolean vatRelated;

    @Schema(description = "Partner vevői kartonhoz kapcsolódik-e", example = "true")
    private boolean customerRelated;

    @Schema(description = "Partner szállítói kartonhoz kapcsolódik-e", example = "false")
    private boolean supplierRelated;

    @Schema(description = "Könyvelhető-e közvetlenül", example = "true")
    private boolean bookable;

    @Schema(description = "Aktív-e a számla", example = "true")
    private boolean active;
}
