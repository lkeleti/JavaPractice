package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AccountingPeriodChangeRequest {
    @Schema(description = "Új lezárt könyvelési időszak vége", example = "2024-12-31")
    private LocalDate closedAccountingPeriod;
}
