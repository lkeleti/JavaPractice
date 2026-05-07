package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.MoneyDirection;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MoneyTransactionCreateRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private MoneyDirection direction;

    @NotNull
    private Long financialAccountId;

    private String description;

    private List<AllocationRequest> allocations;
}