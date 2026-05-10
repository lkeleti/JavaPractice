package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.InvoiceFilterRequest;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.Partner;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceSpecification {

    public static Specification<Invoice> filter(InvoiceFilterRequest f) {
        return (root, query, cb) -> {

            List<Predicate> p = new ArrayList<>();

            // PARTNER JOIN
            Join<Invoice, Partner> partner = null;
            if (f.getPartnerName() != null || f.getPartnerTaxNumber() != null) {
                partner = root.join("partner", JoinType.LEFT);
            }

            // invoiceNumber
            if (f.getInvoiceNumber() != null && !f.getInvoiceNumber().isBlank()) {
                p.add(cb.like(cb.lower(root.get("invoiceNumber")),
                        "%" + f.getInvoiceNumber().toLowerCase() + "%"));
            }

            // issueDate
            if (f.getIssueDateFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("issueDate"), f.getIssueDateFrom()));
            }
            if (f.getIssueDateTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("issueDate"), f.getIssueDateTo()));
            }

            // fulfillmentDate
            if (f.getFulfillmentDateFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("fulfillmentDate"), f.getFulfillmentDateFrom()));
            }
            if (f.getFulfillmentDateTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("fulfillmentDate"), f.getFulfillmentDateTo()));
            }

            // dueDate
            if (f.getDueDateFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("dueDate"), f.getDueDateFrom()));
            }
            if (f.getDueDateTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("dueDate"), f.getDueDateTo()));
            }

            // createdAt
            if (f.getCreatedAtFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.getCreatedAtFrom()));
            }
            if (f.getCreatedAtTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.getCreatedAtTo()));
            }

            // enum mezők
            if (f.getType() != null) {
                p.add(cb.equal(root.get("type"), f.getType()));
            }
            if (f.getCategory() != null) {
                p.add(cb.equal(root.get("category"), f.getCategory()));
            }
            if (f.getStatus() != null) {
                p.add(cb.equal(root.get("status"), f.getStatus()));
            }
            if (f.getNature() != null) {
                p.add(cb.equal(root.get("nature"), f.getNature()));
            }

            // electronicInvoice
            if (f.getElectronicInvoice() != null) {
                p.add(cb.equal(root.get("electronicInvoice"), f.getElectronicInvoice()));
            }

            // netTotal
            if (f.getNetTotalMin() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("netTotal"), f.getNetTotalMin()));
            }
            if (f.getNetTotalMax() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("netTotal"), f.getNetTotalMax()));
            }

            // grossTotal
            if (f.getGrossTotalMin() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("grossTotal"), f.getGrossTotalMin()));
            }
            if (f.getGrossTotalMax() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("grossTotal"), f.getGrossTotalMax()));
            }

            // partnerName
            if (f.getPartnerName() != null && partner != null) {
                p.add(cb.like(cb.lower(partner.get("name")),
                        "%" + f.getPartnerName().toLowerCase() + "%"));
            }

            // partnerTaxNumber
            if (f.getPartnerTaxNumber() != null && partner != null) {
                p.add(cb.equal(partner.get("taxNumber"), f.getPartnerTaxNumber()));
            }

            // originalInvoiceId
            if (f.getOriginalInvoiceId() != null) {
                p.add(cb.equal(root.get("originalInvoice").get("id"), f.getOriginalInvoiceId()));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
