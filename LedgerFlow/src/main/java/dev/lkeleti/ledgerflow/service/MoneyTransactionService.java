package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.AllocationRequest;
import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionFilterRequest;
import dev.lkeleti.ledgerflow.dto.response.CompanyResponse;
import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.MoneyTransactionMapper;
import dev.lkeleti.ledgerflow.repository.FinancialAccountRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import dev.lkeleti.ledgerflow.repository.MoneyTransactionRepository;
import dev.lkeleti.ledgerflow.service.helper.AllocationValidator;
import dev.lkeleti.ledgerflow.service.helper.MoneyTransactionSpecification;
import dev.lkeleti.ledgerflow.service.helper.MoneyTransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dev.lkeleti.ledgerflow.exception.ErrorMessage.MONEY_TRANSACTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MoneyTransactionService {

    private final MoneyTransactionRepository txRepository;
    private final InvoiceRepository invoiceRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MoneyTransactionValidator txValidator;
    private final AllocationValidator allocationValidator;
    private final AccountingService accountingService;
    private final InvoiceStatusService statusService;
    private final MoneyTransactionMapper mapper;
    private final CompanyService companyService;

    @Transactional
    public MoneyTransactionResponse create(MoneyTransactionCreateRequest request) {

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        // 1. Tranzakció dátuma lezárt időszakban?
        if (request.getDate().isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        FinancialAccount fa = financialAccountRepository.findById(request.getFinancialAccountId())
                .filter(FinancialAccount::isActive)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.FINANCIAL_ACCOUNT_NOT_FOUND));

        MoneyTransaction tx = new MoneyTransaction();
        tx.setDate(request.getDate());
        tx.setAmount(request.getAmount());
        tx.setDirection(request.getDirection());
        tx.setFinancialAccount(fa);
        tx.setDescription(request.getDescription());
        tx.setDeleted(false);

        List<Allocation> allocations = new ArrayList<>();

        if (request.getAllocations() != null) {
            for (AllocationRequest ar : request.getAllocations()) {

                Invoice invoice = invoiceRepository.findById(ar.getInvoiceId())
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

                // 2. Allokált számla dátuma lezárt időszakban?
                if (invoice.getIssueDate().isBefore(closed)) {
                    throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
                }

                Allocation allocation = new Allocation();
                allocation.setMoneyTransaction(tx);
                allocation.setInvoice(invoice);
                allocation.setAmount(ar.getAmount());
                allocation.setDeleted(false);

                allocationValidator.validate(allocation);
                allocations.add(allocation);
            }
        }

        tx.setAllocations(allocations);

        txValidator.validate(tx);

        MoneyTransaction saved = txRepository.save(tx);

        accountingService.postMoneyTransaction(saved);

        saved.getAllocations().forEach(a ->
                statusService.updateInvoiceStatus(a.getInvoice())
        );

        return mapper.toResponse(saved);
    }


    @Transactional(readOnly = true)
    public Page<MoneyTransactionResponse> list(MoneyTransactionFilterRequest filter, Pageable pageable) {

        // alapértelmezett rendezés: date DESC
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "date")
            );
        }

        Page<MoneyTransaction> page = txRepository.findAll(
                MoneyTransactionSpecification.filter(filter),
                pageable
        );

        return page.map(mapper::toResponse);
    }


    @Transactional(readOnly = true)
    public MoneyTransactionResponse get(Long id) {
        MoneyTransaction response = txRepository.findById(id)
                .orElseThrow(
                        ()-> new NotFoundException(MONEY_TRANSACTION_NOT_FOUND));
        if (response.isDeleted()) {
            throw new NotFoundException(MONEY_TRANSACTION_NOT_FOUND);
        }
        return mapper.toResponse(response);
    }

    @Transactional(readOnly = true)
    public List<MoneyTransactionResponse> getAllIncludingDeleted() {
        return txRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        MoneyTransaction tx = txRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MONEY_TRANSACTION_NOT_FOUND));

        if (tx.getDate().isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        tx.setDeleted(true);
        tx.getAllocations().forEach(a -> a.setDeleted(true));

        txRepository.save(tx);
    }


    @Transactional
    public MoneyTransactionResponse restore(Long id) {

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        MoneyTransaction tx = txRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MONEY_TRANSACTION_NOT_FOUND));

        if (tx.getDate().isBefore(closed)) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CLOSED);
        }

        tx.setDeleted(false);
        tx.getAllocations().forEach(a -> a.setDeleted(false));

        return mapper.toResponse(txRepository.save(tx));
    }
}
