package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.AllocationResponse;
import dev.lkeleti.ledgerflow.entity.Allocation;
import org.springframework.stereotype.Component;

@Component
public class AllocationMapper {

    public AllocationResponse toResponse(Allocation a) {

        AllocationResponse r = new AllocationResponse();

        r.setId(a.getId());
        r.setInvoiceId(a.getInvoice().getId());
        r.setAmount(a.getAmount());
        r.setDeleted(a.isDeleted());
        r.setCreatedAt(a.getCreatedAt());

        return r;
    }
}
