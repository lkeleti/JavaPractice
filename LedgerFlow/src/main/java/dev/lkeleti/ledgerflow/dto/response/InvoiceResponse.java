package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceStatus;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;

    private LocalDate issueDate;
    private LocalDate fulfillmentDate;
    private LocalDate dueDate;

    private Long partnerId;
    private String partnerName;

    private InvoiceType type;
    private InvoiceCategory category;
    private InvoiceStatus status;

    private BigDecimal netTotal;
    private BigDecimal grossTotal;

    private List<InvoiceVatSummaryResponse> vatSummaries;

    private LocalDateTime createdAt;
}
