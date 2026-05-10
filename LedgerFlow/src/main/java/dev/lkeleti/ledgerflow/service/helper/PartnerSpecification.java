package dev.lkeleti.ledgerflow.service.helper;

import dev.lkeleti.ledgerflow.dto.request.PartnerFilterRequest;
import dev.lkeleti.ledgerflow.entity.Partner;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PartnerSpecification {

    public static Specification<Partner> filter(PartnerFilterRequest f) {
        return (root, query, cb) -> {

            List<Predicate> p = new ArrayList<>();

            // name LIKE
            if (f.getName() != null && !f.getName().isBlank()) {
                p.add(cb.like(cb.lower(root.get("name")), "%" + f.getName().toLowerCase() + "%"));
            }

            // taxNumber exact
            if (f.getTaxNumber() != null && !f.getTaxNumber().isBlank()) {
                p.add(cb.equal(root.get("taxNumber"), f.getTaxNumber()));
            }

            // deleted: true / false / all
            if (f.getDeleted() != null) {
                p.add(cb.equal(root.get("deleted"), f.getDeleted()));
            }

            // privatePerson: true / false / all
            if (f.getPrivatePerson() != null) {
                p.add(cb.equal(root.get("privatePerson"), f.getPrivatePerson()));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
