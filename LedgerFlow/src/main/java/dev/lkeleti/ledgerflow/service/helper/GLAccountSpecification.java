package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.GLAccountFilterRequest;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class GLAccountSpecification {

    public static Specification<GLAccount> filter(GLAccountFilterRequest f) {
        return (root, query, cb) -> {

            List<Predicate> p = new ArrayList<>();

            // number LIKE
            if (f.getNumber() != null && !f.getNumber().isBlank()) {
                p.add(cb.like(cb.lower(root.get("number")),
                        "%" + f.getNumber().toLowerCase() + "%"));
            }

            // name LIKE
            if (f.getName() != null && !f.getName().isBlank()) {
                p.add(cb.like(cb.lower(root.get("name")),
                        "%" + f.getName().toLowerCase() + "%"));
            }

            // type
            if (f.getType() != null) {
                p.add(cb.equal(root.get("type"), f.getType()));
            }

            // active
            if (f.getActive() != null) {
                p.add(cb.equal(root.get("active"), f.getActive()));
            }

            // vatRelated
            if (f.getVatRelated() != null) {
                p.add(cb.equal(root.get("vatRelated"), f.getVatRelated()));
            }

            // customerRelated
            if (f.getCustomerRelated() != null) {
                p.add(cb.equal(root.get("customerRelated"), f.getCustomerRelated()));
            }

            // supplierRelated
            if (f.getSupplierRelated() != null) {
                p.add(cb.equal(root.get("supplierRelated"), f.getSupplierRelated()));
            }

            // bookable
            if (f.getBookable() != null) {
                p.add(cb.equal(root.get("bookable"), f.getBookable()));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
