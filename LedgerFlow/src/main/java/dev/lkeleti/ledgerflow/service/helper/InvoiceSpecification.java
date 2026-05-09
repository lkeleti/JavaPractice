package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.InvoiceFilterRequest;
import dev.lkeleti.ledgerflow.entity.Invoice;
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

            List<Predicate> predicates = new ArrayList<>();

            if (f.getPartnerId() != null) {
                predicates.add(cb.equal(root.get("partner").get("id"), f.getPartnerId()));
            }

            if (f.getInvoiceNumber() != null) {
                predicates.add(cb.like(cb.lower(root.get("invoiceNumber")),
                        "%" + f.getInvoiceNumber().toLowerCase() + "%"));
            }

            if (f.getIssueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("issueDate"), f.getIssueDateFrom()));
            }

            if (f.getIssueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("issueDate"), f.getIssueDateTo()));
            }

            if (f.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), f.getStatus()));
            }

            if (f.getType() != null) {
                predicates.add(cb.equal(root.get("type"), f.getType()));
            }

            if (f.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), f.getCategory()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
