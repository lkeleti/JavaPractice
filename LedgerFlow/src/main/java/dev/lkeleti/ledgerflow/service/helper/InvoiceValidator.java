package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.InvoiceCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.InvoiceUpdateRequest;
import dev.lkeleti.ledgerflow.dto.request.InvoiceVatSummaryRequest;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
    public class InvoiceValidator {

    public void validate(InvoiceCreateRequest request) {
        validateCommon(
                request.getCategory(),
                request.getPartnerId(),
                request.getVatSummaries()
        );
    }

    public void validate(InvoiceUpdateRequest request) {
        validateCommon(
                request.getCategory(),
                request.getPartnerId(),
                request.getVatSummaries()
        );
    }

    private void validateCommon(
            InvoiceCategory category,
            Long partnerId,
            List<InvoiceVatSummaryRequest> vatSummaries
    ) {

        if (category == InvoiceCategory.SZAMLA && partnerId == null) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_PARTNER_REQUIRED);
        }

        if (category == InvoiceCategory.NYUGTA && partnerId != null) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_PARTNER_NOT_ALLOWED_FOR_RECEIPT);
        }

        if (vatSummaries == null || vatSummaries.isEmpty()) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_VAT_SUMMARY_MISSING);
        }

        vatSummaries.forEach(vs -> {
            if (vs.getNetAmount() == null || vs.getVatAmount() == null) {
                throw new BusinessValidationException(ErrorMessage.INVOICE_VAT_SUMMARY_INCOMPLETE);
            }
        });
    }
}
