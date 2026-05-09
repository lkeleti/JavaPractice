package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MoneyTransactionResponse {

    @Schema(description = "A tranzakció azonosítója", example = "10")
    private Long id;

    @Schema(description = "A tranzakció dátuma", example = "2025-05-01")
    private LocalDate date;

    @Schema(description = "A tranzakció összege", example = "150000")
    private BigDecimal amount;

    @Schema(description = "A tranzakció iránya", example = "IN")
    private MoneyDirection direction;

    @Schema(description = "A pénzügyi számla azonosítója", example = "3")
    private Long financialAccountId;

    @Schema(description = "A pénzügyi számla száma", example = "11773322-00000000-12345678")
    private String financialAccountNumber;

    @Schema(description = "Leírás", example = "Banki utalás")
    private String description;

    @Schema(description = "Külső azonosító", example = "BANK-2025-000123")
    private String externalId;

    @Schema(description = "Törölt-e a tranzakció", example = "false")
    private boolean deleted;

    @Schema(description = "Létrehozás ideje", example = "2025-05-01T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Allokációk listája")
    private List<AllocationResponse> allocations;
}
