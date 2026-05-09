package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AllocationRequest {

    @Schema(description = "A számla azonosítója", example = "12")
    @NotNull(message = "A számla megadása kötelező")
    private Long invoiceId;

    @Schema(description = "A kiegyenlített összeg", example = "150000")
    @NotNull(message = "Az összeg megadása kötelező")
    private BigDecimal amount;
}
