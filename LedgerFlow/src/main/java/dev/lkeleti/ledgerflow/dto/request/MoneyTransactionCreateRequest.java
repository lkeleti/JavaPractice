package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MoneyTransactionCreateRequest {

    @Schema(description = "A tranzakció dátuma", example = "2025-05-01")
    @NotNull(message = "A dátum megadása kötelező")
    private LocalDate date;

    @Schema(description = "A tranzakció összege", example = "150000")
    @NotNull(message = "Az összeg megadása kötelező")
    private BigDecimal amount;

    @Schema(description = "A tranzakció iránya (IN vagy OUT)", example = "IN")
    @NotNull(message = "Az irány megadása kötelező")
    private MoneyDirection direction;

    @Schema(description = "A pénzügyi számla azonosítója", example = "3")
    @NotNull(message = "A pénzügyi számla megadása kötelező")
    private Long financialAccountId;

    @Schema(description = "Leírás", example = "Banki utalás")
    private String description;

    @Schema(description = "Allokációk listája")
    private List<AllocationRequest> allocations;
}
