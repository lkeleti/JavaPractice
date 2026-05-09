package dev.lkeleti.ledgerflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingConfigResponse {

    @Schema(description = "Könyvelési beállítás azonosítója", example = "1")
    private Long id;

    @Schema(description = "Árbevétel számla azonosítója", example = "1")
    private Long revenueAccountId;
    @Schema(description = "Árbevétel számla száma", example = "91")
    private String revenueAccountNumber;
    @Schema(description = "Árbevétel számla megnevezése", example = "Belföldi értékesítés árbevétele")
    private String revenueAccountName;

    @Schema(description = "Ráfordítás számla azonosítója", example = "2")
    private Long expenseAccountId;
    @Schema(description = "Ráfordítás számla száma", example = "51")
    private String expenseAccountNumber;
    @Schema(description = "Ráfordítás számla megnevezése", example = "Anyagköltség")
    private String expenseAccountName;

    @Schema(description = "Fizetendő ÁFA számla azonosítója", example = "3")
    private Long vatPayableAccountId;
    @Schema(description = "Fizetendő ÁFA számla száma", example = "467")
    private String vatPayableAccountNumber;
    @Schema(description = "Fizetendő ÁFA számla megnevezése", example = "Fizetendő ÁFA")
    private String vatPayableAccountName;

    @Schema(description = "Visszaigényelhető ÁFA számla azonosítója", example = "4")
    private Long vatReceivableAccountId;
    @Schema(description = "Visszaigényelhető ÁFA számla száma", example = "466")
    private String vatReceivableAccountNumber;
    @Schema(description = "Visszaigényelhető ÁFA számla megnevezése", example = "Előzetesen felszámított ÁFA")
    private String vatReceivableAccountName;
}
