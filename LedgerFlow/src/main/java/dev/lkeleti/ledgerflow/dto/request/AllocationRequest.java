package dev.lkeleti.ledgerflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AllocationRequest {

    @NotNull
    private Long invoiceId;

    @NotNull
    private BigDecimal amount;
}