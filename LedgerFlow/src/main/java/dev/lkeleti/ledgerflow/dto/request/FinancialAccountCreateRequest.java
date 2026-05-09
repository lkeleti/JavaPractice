package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.FinancialAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAccountCreateRequest {

    @Schema(description = "A pénzügyi számla neve", example = "OTP főszámla")
    @NotBlank(message = "A név megadása kötelező")
    @Size(max = 100, message = "A név legfeljebb 100 karakter lehet")
    private String name;

    @Schema(description = "A számla típusa", example = "BANK")
    @NotNull(message = "A számla típusa kötelező")
    private FinancialAccountType type;

    @Schema(description = "Bankszámlaszám", example = "11773322-00000000-12345678")
    @Size(max = 50, message = "A bankszámlaszám legfeljebb 50 karakter lehet")
    private String accountNumber;

    @Schema(description = "IBAN szám", example = "HU42117733220000000012345678")
    @Size(max = 34, message = "Az IBAN legfeljebb 34 karakter lehet")
    private String iban;

    @Schema(description = "SWIFT kód", example = "OTPVHUHB")
    @Size(max = 20, message = "A SWIFT kód legfeljebb 20 karakter lehet")
    private String swift;

    @Schema(description = "Kapcsolt főkönyvi számla azonosítója", example = "12")
    private Long glAccountId;
}
