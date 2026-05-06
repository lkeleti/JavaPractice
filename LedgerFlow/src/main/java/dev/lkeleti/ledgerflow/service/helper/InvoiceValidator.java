package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceCategory;
import org.springframework.stereotype.Component;

@Component
public class InvoiceValidator {

    public void validate(Invoice invoice) {

        if (invoice.getCategory() == InvoiceCategory.SZAMLA
                && invoice.getPartner() == null) {
            throw new IllegalStateException("Számlához partner kötelező");
        }

        if (invoice.getCategory() == InvoiceCategory.NYUGTA
                && invoice.getPartner() != null) {
            throw new IllegalStateException("Nyugtához nem tartozhat partner");
        }
    }
}