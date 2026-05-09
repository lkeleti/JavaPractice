package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingConfigRequest {

    @Schema(description = "Árbevétel főkönyvi számla azonosítója", example = "1")
    private Long revenueAccountId;

    @Schema(description = "Ráfordítás főkönyvi számla azonosítója", example = "2")
    private Long expenseAccountId;

    @Schema(description = "Fizetendő ÁFA főkönyvi számla azonosítója", example = "3")
    private Long vatPayableAccountId;

    @Schema(description = "Visszaigényelhető ÁFA főkönyvi számla azonosítója", example = "4")
    private Long vatReceivableAccountId;
}
