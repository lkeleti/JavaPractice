package dev.lkeleti.ledgerflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompanyResponse {

    @Schema(description = "Cég azonosító")
    private Long id;

    @Schema(description = "Cégnév")
    private String name;

    @Schema(description = "Adószám")
    private String taxNumber;

    @Schema(description = "Irányítószám")
    private String postalCode;

    @Schema(description = "Város")
    private String city;

    @Schema(description = "Közterület neve")
    private String streetName;

    @Schema(description = "Közterület típusa")
    private String streetType;

    @Schema(description = "Házszám")
    private String houseNumber;

    @Schema(description = "Lezárt könyvelési időszak vége")
    private LocalDate closedAccountingPeriod;
}
