package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.VatCodeResponse;
import dev.lkeleti.ledgerflow.entity.VatCode;
import org.springframework.stereotype.Component;

@Component
public class VatCodeMapper {

    public VatCodeResponse toResponse(VatCode v) {

        VatCodeResponse r = new VatCodeResponse();

        r.setId(v.getId());
        r.setCode(v.getCode());
        r.setName(v.getName());
        r.setRate(v.getRate());
        r.setType(v.getType());
        r.setDeductible(v.isDeductible());
        r.setActive(v.isActive());
        r.setDeleted(v.isDeleted());
        r.setCreatedAt(v.getCreatedAt());

        return r;
    }
}
