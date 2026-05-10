package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceCorrectionRequest {

    @Schema(description = "A helyesbítő számla kiállítási dátuma", example = "2024-05-10")
    private LocalDate issueDate;

    @Schema(description = "Új nettó összeg (különbözet)", example = "15000.00")
    private BigDecimal netDifference;

    @Schema(description = "Új bruttó összeg (különbözet)", example = "19050.00")
    private BigDecimal grossDifference;

    @Schema(description = "ÁFA bontás különbözet")
    private List<VatDifferenceItem> vatDifferences;

    @Getter
    @Setter
    public static class VatDifferenceItem {
        private Long vatCodeId;
        private BigDecimal netAmount;
        private BigDecimal vatAmount;
    }
}
