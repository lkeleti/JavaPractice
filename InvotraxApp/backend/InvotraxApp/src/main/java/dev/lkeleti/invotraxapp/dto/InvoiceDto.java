package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceDto {
    private Long id;
    private Long sellerId;
    private Long buyerId;

    private String sellerName;
    private String sellerTaxNumber;
    private String sellerAddress;
    private String sellerHeadOfficeAddress;
    private String sellerDefaultBranchAddress;
    private String sellerCompanyRegNumber;
    private String sellerBankDetails;
    private String sellerIban;
    private String buyerName;
    private String buyerTaxNumber;
    private String buyerAddress;
    private String invoiceTypeName;
    private String paymentMethodName;

    private LocalDate issuedAt;
    private LocalDate fulfillmentAt;
    private LocalDate dueDate;
    private String invoiceType;
    private String paymentMethod;
    private BigDecimal netTotal;
    private BigDecimal grossTotal;
    private String invoiceNumber;
    private String pdfContentBase64;
    private List<InvoiceItemDto> items;
}
