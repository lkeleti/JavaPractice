package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.PaymentMethodResponse;
import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    public PaymentMethodResponse toResponse(PaymentMethod pm) {

        PaymentMethodResponse r = new PaymentMethodResponse();

        r.setId(pm.getId());
        r.setName(pm.getName());
        r.setCode(pm.getCode());
        r.setFinancial(pm.isFinancial());
        r.setCash(pm.isCash());
        r.setActive(pm.isActive());
        r.setCreatedAt(pm.getCreatedAt());

        return r;
    }
}
