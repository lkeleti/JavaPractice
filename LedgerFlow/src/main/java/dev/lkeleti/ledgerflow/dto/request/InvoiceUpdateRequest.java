package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceUpdateRequest {

    @NotNull(message = "A számla azonosító kötelező")
    private Long id;

    @NotBlank(message = "A számlaszám kötelező")
    private String invoiceNumber;

    @NotNull(message = "A kiállítás dátuma kötelező")
    private LocalDate issueDate;

    @NotNull(message = "A teljesítés dátuma kötelező")
    private LocalDate fulfillmentDate;

    @NotNull(message = "A fizetési határidő kötelező")
    private LocalDate paymentDueDate;

    private Long partnerId;

    @NotNull(message = "A számla típusa kötelező")
    private InvoiceType type;

    @NotNull(message = "A számla kategóriája kötelező")
    private InvoiceCategory category;

    @NotNull(message = "A nettó összeg kötelező")
    private BigDecimal netTotal;

    @NotNull(message = "A bruttó összeg kötelező")
    private BigDecimal grossTotal;

    @NotNull(message = "Az ÁFA bontás kötelező")
    private List<InvoiceVatSummaryRequest> vatSummaries;
}
