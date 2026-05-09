package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.GLAccountResponse;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import org.springframework.stereotype.Component;

@Component
public class GLAccountMapper {

    public GLAccountResponse toResponse(GLAccount account) {

        if (account == null) {
            return null;
        }

        GLAccountResponse r = new GLAccountResponse();

        r.setId(account.getId());
        r.setNumber(account.getNumber());
        r.setName(account.getName());
        r.setType(account.getType());
        r.setVatRelated(account.isVatRelated());
        r.setCustomerRelated(account.isCustomerRelated());
        r.setSupplierRelated(account.isSupplierRelated());
        r.setBookable(account.isBookable());
        r.setActive(account.isActive());

        return r;
    }
}
