package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLAccountUpdateRequest {

    @Schema(description = "A főkönyvi számla száma", example = "311")
    @NotBlank(message = "A számlaszám megadása kötelező")
    @Size(max = 20, message = "A számlaszám legfeljebb 20 karakter lehet")
    private String number;

    @Schema(description = "A főkönyvi számla megnevezése", example = "Vevők")
    @NotBlank(message = "A név megadása kötelező")
    @Size(max = 100, message = "A név legfeljebb 100 karakter lehet")
    private String name;

    @Schema(description = "A számla típusa", example = "ASSET")
    @NotNull(message = "A számla típusa kötelező")
    private GLAccountType type;

    @Schema(description = "ÁFA-hoz kapcsolódik-e", example = "false")
    private boolean vatRelated;

    @Schema(description = "Vevői kartonhoz kapcsolódik-e", example = "true")
    private boolean customerRelated;

    @Schema(description = "Szállítói kartonhoz kapcsolódik-e", example = "false")
    private boolean supplierRelated;

    @Schema(description = "Könyvelhető-e közvetlenül", example = "true")
    private boolean bookable;

    @Schema(description = "Aktív-e a számla", example = "true")
    private boolean active;
}
