package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodCreateRequest {

    @Schema(description = "A fizetési mód neve", example = "Átutalás")
    @NotBlank(message = "A név megadása kötelező")
    @Size(max = 50, message = "A név legfeljebb 50 karakter lehet")
    private String name;

    @Schema(description = "A fizetési mód kódja", example = "TRANSFER")
    @NotBlank(message = "A kód megadása kötelező")
    @Size(max = 30, message = "A kód legfeljebb 30 karakter lehet")
    private String code;

    @Schema(description = "Generál-e pénzmozgást", example = "true")
    private boolean financial = true;

    @Schema(description = "Készpénzes fizetés-e", example = "false")
    private boolean cash;
}
