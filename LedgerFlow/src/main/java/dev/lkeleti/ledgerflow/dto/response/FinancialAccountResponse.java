package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.FinancialAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAccountResponse {

    @Schema(description = "A pénzügyi számla azonosítója", example = "1")
    private Long id;

    @Schema(description = "A pénzügyi számla neve", example = "OTP főszámla")
    private String name;

    @Schema(description = "A számla típusa", example = "BANK")
    private FinancialAccountType type;

    @Schema(description = "Bankszámlaszám", example = "11773322-00000000-12345678")
    private String accountNumber;

    @Schema(description = "IBAN szám", example = "HU42117733220000000012345678")
    private String iban;

    @Schema(description = "SWIFT kód", example = "OTPVHUHB")
    private String swift;

    @Schema(description = "Kapcsolt főkönyvi számla azonosítója", example = "12")
    private Long glAccountId;

    @Schema(description = "Kapcsolt főkönyvi számla száma", example = "384")
    private String glAccountNumber;

    @Schema(description = "A számla aktív-e", example = "true")
    private boolean active;
}
