package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceStatus;
import dev.lkeleti.ledgerflow.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoiceStatusService {

    private final AllocationRepository allocationRepository;

    public InvoiceStatus calculateStatus(Invoice invoice) {

        BigDecimal paid = allocationRepository.findByInvoiceId(invoice.getId())
                .stream()
                .map(Allocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gross = invoice.getGrossTotal();

        int cmp = paid.compareTo(gross);

        if (cmp == 0) {
            return InvoiceStatus.KIFIZETETT;
        } else if (cmp > 0) {
            throw new IllegalStateException("Túlfizetés történt: invoiceId=" + invoice.getId());
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            return InvoiceStatus.RESZBEN_FIZETETT;
        } else {
            return InvoiceStatus.NYITOTT;
        }
    }

    @Transactional
    public void updateInvoiceStatus(Invoice invoice) {
        InvoiceStatus newStatus = calculateStatus(invoice);
        invoice.setStatus(newStatus);
    }
}