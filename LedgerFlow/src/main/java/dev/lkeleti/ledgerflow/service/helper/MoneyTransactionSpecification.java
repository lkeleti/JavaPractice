package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionFilterRequest;
import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import dev.lkeleti.ledgerflow.entity.Partner;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MoneyTransactionSpecification {

    public static Specification<MoneyTransaction> filter(MoneyTransactionFilterRequest f) {
        return (root, query, cb) -> {

            List<Predicate> p = new ArrayList<>();

            // Alap JOIN-ok csak akkor, ha kell
            Join<MoneyTransaction, Allocation> allocation = null;
            Join<Allocation, Invoice> invoice = null;
            Join<Invoice, Partner> partner = null;

            boolean needsAllocationJoin =
                    f.getInvoiceId() != null ||
                            (f.getHasAllocation() != null) ||
                            (f.getInvoiceNumber() != null) ||
                            (f.getPartnerName() != null) ||
                            (f.getPartnerTaxNumber() != null);

            if (needsAllocationJoin) {
                allocation = root.join("allocations", JoinType.LEFT);
                invoice = allocation.join("invoice", JoinType.LEFT);
            }

            if (f.getPartnerName() != null || f.getPartnerTaxNumber() != null) {
                if (invoice == null) {
                    allocation = root.join("allocations", JoinType.LEFT);
                    invoice = allocation.join("invoice", JoinType.LEFT);
                }
                partner = invoice.join("partner", JoinType.LEFT);
            }

            // date
            if (f.getDateFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("date"), f.getDateFrom()));
            }
            if (f.getDateTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("date"), f.getDateTo()));
            }

            // amount
            if (f.getAmountMin() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("amount"), f.getAmountMin()));
            }
            if (f.getAmountMax() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("amount"), f.getAmountMax()));
            }

            // direction
            if (f.getDirection() != null) {
                p.add(cb.equal(root.get("direction"), f.getDirection()));
            }

            // financialAccount
            if (f.getFinancialAccountId() != null) {
                p.add(cb.equal(root.get("financialAccount").get("id"), f.getFinancialAccountId()));
            }

            // description
            if (f.getDescription() != null && !f.getDescription().isBlank()) {
                p.add(cb.like(cb.lower(root.get("description")),
                        "%" + f.getDescription().toLowerCase() + "%"));
            }

            // externalId
            if (f.getExternalId() != null && !f.getExternalId().isBlank()) {
                p.add(cb.equal(root.get("externalId"), f.getExternalId()));
            }

            // deleted
            if (f.getDeleted() != null) {
                p.add(cb.equal(root.get("deleted"), f.getDeleted()));
            }

            // createdAt
            if (f.getCreatedAtFrom() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.getCreatedAtFrom()));
            }
            if (f.getCreatedAtTo() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.getCreatedAtTo()));
            }

            // invoiceId
            if (f.getInvoiceId() != null && invoice != null) {
                p.add(cb.equal(invoice.get("id"), f.getInvoiceId()));
            }

            // invoiceNumber
            if (f.getInvoiceNumber() != null && !f.getInvoiceNumber().isBlank() && invoice != null) {
                p.add(cb.like(cb.lower(invoice.get("invoiceNumber")),
                        "%" + f.getInvoiceNumber().toLowerCase() + "%"));
            }

            // partnerName
            if (f.getPartnerName() != null && !f.getPartnerName().isBlank() && partner != null) {
                p.add(cb.like(cb.lower(partner.get("name")),
                        "%" + f.getPartnerName().toLowerCase() + "%"));
            }

            // partnerTaxNumber
            if (f.getPartnerTaxNumber() != null && !f.getPartnerTaxNumber().isBlank() && partner != null) {
                p.add(cb.equal(partner.get("taxNumber"), f.getPartnerTaxNumber()));
            }

            // hasAllocation
            if (f.getHasAllocation() != null) {
                if (Boolean.TRUE.equals(f.getHasAllocation())) {
                    p.add(cb.isNotEmpty(root.get("allocations")));
                } else {
                    p.add(cb.isEmpty(root.get("allocations")));
                }
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
