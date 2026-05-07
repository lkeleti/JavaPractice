package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceCreateRequest {

    @NotNull
    private String invoiceNumber;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate fulfillmentDate;

    @NotNull
    private LocalDate paymentDueDate;

    @NotNull
    private Long partnerId;

    @NotNull
    private InvoiceType type;

    @NotNull
    private BigDecimal netTotal;

    @NotNull
    private BigDecimal grossTotal;

    private List<InvoiceVatSummaryRequest> vatSummaries;
}