package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.AllocationRequest;
import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.MoneyTransactionMapper;
import dev.lkeleti.ledgerflow.repository.FinancialAccountRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import dev.lkeleti.ledgerflow.repository.MoneyTransactionRepository;
import dev.lkeleti.ledgerflow.service.helper.AllocationValidator;
import dev.lkeleti.ledgerflow.service.helper.MoneyTransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public MoneyTransactionResponse create(MoneyTransactionCreateRequest request) {

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

        // ALLOCATION-ÖK
        List<Allocation> allocations = new ArrayList<>();

        if (request.getAllocations() != null) {
            for (AllocationRequest ar : request.getAllocations()) {

                Invoice invoice = invoiceRepository.findById(ar.getInvoiceId())
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.INVOICE_NOT_FOUND));

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
    public MoneyTransactionResponse get(Long id) {

        MoneyTransaction tx = txRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MONEY_TRANSACTION_NOT_FOUND));

        return mapper.toResponse(tx);
    }

    @Transactional(readOnly = true)
    public List<MoneyTransactionResponse> getAll() {
        return txRepository.findAll()
                .stream()
                .filter(tx -> !tx.isDeleted())
                .map(mapper::toResponse)
                .toList();
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

        MoneyTransaction tx = txRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MONEY_TRANSACTION_NOT_FOUND));

        tx.setDeleted(true);
        tx.getAllocations().forEach(a -> a.setDeleted(true));

        txRepository.save(tx);
    }

    @Transactional
    public MoneyTransactionResponse restore(Long id) {

        MoneyTransaction tx = txRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MONEY_TRANSACTION_NOT_FOUND));

        tx.setDeleted(false);
        tx.getAllocations().forEach(a -> a.setDeleted(false));

        return mapper.toResponse(txRepository.save(tx));
    }
}
