package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.InvoiceCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.InvoiceResponse;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.InvoiceVatSummary;
import dev.lkeleti.ledgerflow.entity.Partner;
import dev.lkeleti.ledgerflow.entity.VatCode;
import dev.lkeleti.ledgerflow.repository.PartnerRepository;
import dev.lkeleti.ledgerflow.repository.VatCodeRepository;
import dev.lkeleti.ledgerflow.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PartnerRepository partnerRepository;
    private final VatCodeRepository vatCodeRepository;

    @PostMapping
    public InvoiceResponse create(
            @Valid @RequestBody InvoiceCreateRequest request
    ) {

        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow();

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setFulfillmentDate(request.getFulfillmentDate());
        invoice.setDueDate(request.getPaymentDueDate());
        invoice.setPartner(partner);
        invoice.setType(request.getType());
        invoice.setNetTotal(request.getNetTotal());
        invoice.setGrossTotal(request.getGrossTotal());

        invoice.setVatSummaries(new ArrayList<>());

        request.getVatSummaries().forEach(v -> {

            VatCode vatCode = vatCodeRepository.findById(v.getVatCodeId())
                    .orElseThrow();

            InvoiceVatSummary vs = new InvoiceVatSummary();

            vs.setInvoice(invoice);
            vs.setVatCode(vatCode);
            vs.setNetAmount(v.getNetAmount());
            vs.setVatAmount(v.getVatAmount());

            invoice.getVatSummaries().add(vs);
        });

        Invoice saved = invoiceService.createInvoice(invoice);

        InvoiceResponse response = new InvoiceResponse();
        response.setId(saved.getId());
        response.setInvoiceNumber(saved.getInvoiceNumber());
        response.setStatus(saved.getStatus().name());

        return response;
    }
}