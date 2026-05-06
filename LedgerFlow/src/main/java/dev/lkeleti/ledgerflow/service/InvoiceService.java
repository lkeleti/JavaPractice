package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import dev.lkeleti.ledgerflow.service.helper.InvoiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceValidator invoiceValidator;
    private final AccountingService accountingService;
    private final InvoiceStatusService statusService;

    @Transactional
    public Invoice createInvoice(Invoice invoice) {

        // 1. VALIDÁCIÓ
        invoiceValidator.validate(invoice);

        invoice.setCreatedAt(LocalDateTime.now());

        // 2. MENTÉS
        Invoice saved = invoiceRepository.save(invoice);

        // 3. KÖNYVELÉS
        accountingService.postInvoice(saved);

        // 4. STÁTUSZ
        statusService.updateInvoiceStatus(saved);

        return saved;
    }
}