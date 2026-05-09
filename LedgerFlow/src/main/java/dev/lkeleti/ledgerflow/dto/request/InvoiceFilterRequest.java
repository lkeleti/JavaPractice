package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceStatus;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InvoiceFilterRequest {

    private Long partnerId;
    private String invoiceNumber;

    private LocalDate issueDateFrom;
    private LocalDate issueDateTo;

    private InvoiceStatus status;
    private InvoiceType type;
    private InvoiceCategory category;
}
