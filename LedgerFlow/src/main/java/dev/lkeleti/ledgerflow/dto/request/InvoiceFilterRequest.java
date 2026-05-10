package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceNature;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceStatus;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceFilterRequest {

    private String invoiceNumber;

    private LocalDate issueDateFrom;
    private LocalDate issueDateTo;

    private LocalDate fulfillmentDateFrom;
    private LocalDate fulfillmentDateTo;

    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;

    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;

    private InvoiceType type;
    private InvoiceCategory category;
    private InvoiceStatus status;
    private InvoiceNature nature;

    private Boolean electronicInvoice; // null = all

    private BigDecimal netTotalMin;
    private BigDecimal netTotalMax;

    private BigDecimal grossTotalMin;
    private BigDecimal grossTotalMax;

    // Partner JOIN mezők
    private String partnerName;
    private String partnerTaxNumber;

    // Speciális
    private Long originalInvoiceId;
}
