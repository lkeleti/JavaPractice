package dev.lkeleti.ledgerflow.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {

    private Long id;

    private String invoiceNumber;

    private String status;
}