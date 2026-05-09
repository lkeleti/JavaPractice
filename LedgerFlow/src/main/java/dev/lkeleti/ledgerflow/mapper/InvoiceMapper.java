package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.request.InvoiceCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.InvoiceUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.InvoiceResponse;
import dev.lkeleti.ledgerflow.dto.response.InvoiceVatSummaryResponse;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.InvoiceVatSummary;
import dev.lkeleti.ledgerflow.entity.Partner;
import dev.lkeleti.ledgerflow.entity.VatCode;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceStatus;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.repository.PartnerRepository;
import dev.lkeleti.ledgerflow.repository.VatCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final PartnerRepository partnerRepository;
    private final VatCodeRepository vatCodeRepository;

    public Invoice toEntity(InvoiceCreateRequest request) {

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setFulfillmentDate(request.getFulfillmentDate());
        invoice.setDueDate(request.getPaymentDueDate());
        invoice.setType(request.getType());
        invoice.setCategory(request.getCategory());
        invoice.setNetTotal(request.getNetTotal());
        invoice.setGrossTotal(request.getGrossTotal());
        invoice.setStatus(InvoiceStatus.NYITOTT);

        if (request.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND));
            invoice.setPartner(partner);
        }

        List<InvoiceVatSummary> vatSummaries = new ArrayList<>();

        request.getVatSummaries().forEach(v -> {
            VatCode vatCode = vatCodeRepository.findById(v.getVatCodeId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND));

            InvoiceVatSummary vs = new InvoiceVatSummary();
            vs.setInvoice(invoice);
            vs.setVatCode(vatCode);
            vs.setNetAmount(v.getNetAmount());
            vs.setVatAmount(v.getVatAmount());

            vatSummaries.add(vs);
        });

        invoice.setVatSummaries(vatSummaries);

        return invoice;
    }

    public void updateEntity(Invoice invoice, InvoiceUpdateRequest request) {

        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setFulfillmentDate(request.getFulfillmentDate());
        invoice.setDueDate(request.getPaymentDueDate());
        invoice.setType(request.getType());
        invoice.setCategory(request.getCategory());
        invoice.setNetTotal(request.getNetTotal());
        invoice.setGrossTotal(request.getGrossTotal());

        if (request.getPartnerId() != null) {
            Partner partner = partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND));
            invoice.setPartner(partner);
        } else {
            invoice.setPartner(null);
        }

        invoice.getVatSummaries().clear();

        request.getVatSummaries().forEach(v -> {
            VatCode vatCode = vatCodeRepository.findById(v.getVatCodeId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND));

            InvoiceVatSummary vs = new InvoiceVatSummary();
            vs.setInvoice(invoice);
            vs.setVatCode(vatCode);
            vs.setNetAmount(v.getNetAmount());
            vs.setVatAmount(v.getVatAmount());

            invoice.getVatSummaries().add(vs);
        });
    }

    public InvoiceResponse toResponse(Invoice invoice) {

        InvoiceResponse r = new InvoiceResponse();

        r.setId(invoice.getId());
        r.setInvoiceNumber(invoice.getInvoiceNumber());

        r.setIssueDate(invoice.getIssueDate());
        r.setFulfillmentDate(invoice.getFulfillmentDate());
        r.setDueDate(invoice.getDueDate());

        if (invoice.getPartner() != null) {
            r.setPartnerId(invoice.getPartner().getId());
            r.setPartnerName(invoice.getPartner().getName());
        }

        r.setType(invoice.getType());
        r.setCategory(invoice.getCategory());
        r.setStatus(invoice.getStatus());

        r.setNetTotal(invoice.getNetTotal());
        r.setGrossTotal(invoice.getGrossTotal());

        List<InvoiceVatSummaryResponse> vatResponses = invoice.getVatSummaries()
                .stream()
                .map(vs -> {
                    InvoiceVatSummaryResponse v = new InvoiceVatSummaryResponse();
                    v.setVatCodeId(vs.getVatCode().getId());
                    v.setVatCode(vs.getVatCode().getCode());
                    v.setNetAmount(vs.getNetAmount());
                    v.setVatAmount(vs.getVatAmount());
                    return v;
                })
                .toList();

        r.setVatSummaries(vatResponses);

        r.setCreatedAt(invoice.getCreatedAt());

        return r;
    }
}
