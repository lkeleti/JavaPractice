package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AllocationValidator {

    private final AllocationRepository allocationRepository;

    public void validate(Allocation allocation) {

        if (allocation.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Allocation összeg > 0 kell legyen");
        }

        Invoice invoice = allocation.getInvoice();

        BigDecimal alreadyPaid = allocationRepository
                .findByInvoiceId(invoice.getId())
                .stream()
                .map(Allocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (alreadyPaid.add(allocation.getAmount())
                .compareTo(invoice.getGrossTotal()) > 0) {
            throw new IllegalStateException("Túlfizetés nem megengedett");
        }
    }
}