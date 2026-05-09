package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.InvoiceCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.InvoiceFilterRequest;
import dev.lkeleti.ledgerflow.dto.request.InvoiceUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.InvoiceResponse;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.InvoiceMapper;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import dev.lkeleti.ledgerflow.service.helper.InvoiceSpecification;
import dev.lkeleti.ledgerflow.service.helper.InvoiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceValidator invoiceValidator;
    private final InvoiceMapper invoiceMapper;
    private final AccountingService accountingService;
    private final InvoiceStatusService statusService;

    @Transactional
    public InvoiceResponse create(InvoiceCreateRequest request) {

        invoiceValidator.validate(request);

        Invoice invoice = invoiceMapper.toEntity(request);

        Invoice saved = invoiceRepository.save(invoice);

        accountingService.postInvoice(saved);

        statusService.updateInvoiceStatus(saved);

        return invoiceMapper.toResponse(saved);
    }

    @Transactional
    public InvoiceResponse update(InvoiceUpdateRequest request) {

        invoiceValidator.validate(request);

        Invoice invoice = invoiceRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

        invoiceMapper.updateEntity(invoice, request);

        Invoice saved = invoiceRepository.save(invoice);

        accountingService.postInvoice(saved);

        statusService.updateInvoiceStatus(saved);

        return invoiceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

        return invoiceMapper.toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> list(InvoiceFilterRequest filter, Pageable pageable) {

        Page<Invoice> page = invoiceRepository.findAll(
                InvoiceSpecification.filter(filter),
                pageable
        );

        return page.map(invoiceMapper::toResponse);
    }

}
