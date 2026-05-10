package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCreateRequest {

    @Schema(description = "Cégnév", example = "LedgerFlow Kft.")
    private String name;

    @Schema(description = "Adószám", example = "12345678-2-42")
    private String taxNumber;

    @Schema(description = "Irányítószám", example = "5000")
    private String postalCode;

    @Schema(description = "Város", example = "Szolnok")
    private String city;

    @Schema(description = "Közterület neve", example = "Kossuth Lajos")
    private String streetName;

    @Schema(description = "Közterület típusa", example = "utca")
    private String streetType;

    @Schema(description = "Házszám", example = "12")
    private String houseNumber;
}
