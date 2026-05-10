package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MoneyTransactionFilterRequest {

    @Schema(description = "Dátum intervallum - kezdő dátum", example = "2024-01-01")
    private LocalDate dateFrom;

    @Schema(description = "Dátum intervallum - záró dátum", example = "2024-12-31")
    private LocalDate dateTo;

    @Schema(description = "Minimum összeg", example = "0")
    private BigDecimal amountMin;

    @Schema(description = "Maximum összeg", example = "1000000")
    private BigDecimal amountMax;

    @Schema(description = "Pénzmozgás iránya (IN/OUT)")
    private MoneyDirection direction;

    @Schema(description = "Pénzügyi számla azonosítója")
    private Long financialAccountId;

    @Schema(description = "Leírás szerinti szűrés (LIKE)", example = "bér")
    private String description;

    @Schema(description = "Külső azonosító szerinti szűrés", example = "BANK-12345")
    private String externalId;

    @Schema(
            description = "Törölt státusz szűrése. false = csak aktív, true = csak törölt, null = mind",
            example = "false"
    )
    private Boolean deleted = false;

    // Kapcsolt mezők – Allocation → Invoice → Partner

    @Schema(description = "Partner név szerinti szűrés (LIKE)", example = "Kft")
    private String partnerName;

    @Schema(description = "Partner adószám szerinti szűrés", example = "12345678-1-42")
    private String partnerTaxNumber;

    @Schema(description = "Számlaszám szerinti szűrés", example = "INV-2024-00123")
    private String invoiceNumber;

    @Schema(description = "Számla azonosító szerinti szűrés")
    private Long invoiceId;

    @Schema(
            description = "Csak allokált / nem allokált tranzakciók. true = csak allokált, false = csak nem allokált, null = mind",
            example = "null"
    )
    private Boolean hasAllocation;

    @Schema(description = "Létrehozás dátuma - tól", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAtFrom;

    @Schema(description = "Létrehozás dátuma - ig", example = "2024-12-31T23:59:59")
    private LocalDateTime createdAtTo;
}
