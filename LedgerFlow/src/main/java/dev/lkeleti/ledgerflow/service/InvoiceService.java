package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.*;
import dev.lkeleti.ledgerflow.dto.response.CompanyResponse;
import dev.lkeleti.ledgerflow.dto.response.InvoiceResponse;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.enums.InvoiceNature;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
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

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceValidator invoiceValidator;
    private final InvoiceMapper invoiceMapper;
    private final AccountingService accountingService;
    private final InvoiceStatusService statusService;
    private final CompanyService companyService;

    @Transactional
    public InvoiceResponse create(InvoiceCreateRequest request) {

        CompanyResponse company = companyService.get();

        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        if (request.getIssueDate().isBefore(closed)
                || request.getFulfillmentDate().isBefore(closed)
                || request.getPaymentDueDate().isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        invoiceValidator.validate(request);

        Invoice invoice = invoiceMapper.toEntity(request);

        Invoice saved = invoiceRepository.save(invoice);

        accountingService.postInvoice(saved);

        statusService.updateInvoiceStatus(saved);

        return invoiceMapper.toResponse(saved);
    }

    @Transactional
    public InvoiceResponse update(InvoiceUpdateRequest request) {

        Invoice invoice = invoiceRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

        CompanyResponse company = companyService.get();

        // 1. Eredeti számla dátuma lezárt időszakban van?
        if (invoice.getIssueDate().isBefore(company.getClosedAccountingPeriod().plusDays(1))) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        // 2. Új dátum lezárt időszakban van?
        if (request.getIssueDate().isBefore(company.getClosedAccountingPeriod().plusDays(1))) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }


        invoiceValidator.validate(request);

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

    @Transactional
    public InvoiceResponse createStorno(Long originalId, InvoiceStornoRequest request) {

        Invoice original = invoiceRepository.findById(originalId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

        if (original.getNature() == InvoiceNature.STORNO) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_ALREADY_STORNO);
        }

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);
        LocalDate issueDate = LocalDate.parse(request.getIssueDate());

        if (issueDate.isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_CANNOT_STORNO_CLOSED_PERIOD);
        }

        invoiceValidator.validateStorno(original, issueDate, closed);
        Invoice storno = invoiceMapper.copyForStorno(original, issueDate);

        Invoice saved = invoiceRepository.save(storno);

        accountingService.postInvoice(saved);
        statusService.updateInvoiceStatus(saved);

        return invoiceMapper.toResponse(saved);
    }


    @Transactional
    public InvoiceResponse createCorrection(Long originalId, InvoiceCorrectionRequest request) {

        Invoice original = invoiceRepository.findById(originalId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

        if (original.getNature() == InvoiceNature.STORNO) {
            throw new BusinessValidationException(ErrorMessage.INVOICE_CANNOT_CORRECT_STORNO);
        }

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        if (request.getIssueDate().isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        invoiceValidator.validateCorrection(original, request, closed);
        Invoice correction = invoiceMapper.copyForCorrection(original, request);

        Invoice saved = invoiceRepository.save(correction);

        accountingService.postInvoice(saved);
        statusService.updateInvoiceStatus(saved);

        return invoiceMapper.toResponse(saved);
    }
}
