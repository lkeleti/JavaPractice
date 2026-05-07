package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.AllocationRequest;
import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.entity.MoneyTransaction;
import dev.lkeleti.ledgerflow.repository.FinancialAccountRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import dev.lkeleti.ledgerflow.service.MoneyTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/money-transactions")
@RequiredArgsConstructor
public class MoneyTransactionController {

    private final MoneyTransactionService moneyTransactionService;
    private final InvoiceRepository invoiceRepository;
    private final FinancialAccountRepository financialAccountRepository;

    @PostMapping
    public MoneyTransactionResponse create(
            @Valid @RequestBody MoneyTransactionCreateRequest request
    ) {

        FinancialAccount financialAccount =
                financialAccountRepository.findById(request.getFinancialAccountId())
                        .orElseThrow(() -> new RuntimeException("Financial account not found"));

        MoneyTransaction tx = new MoneyTransaction();

        tx.setDate(request.getDate());
        tx.setAmount(request.getAmount());
        tx.setDirection(request.getDirection());
        tx.setFinancialAccount(financialAccount);
        tx.setDescription(request.getDescription());

        tx.setAllocations(new ArrayList<>());

        if (request.getAllocations() != null) {

            for (AllocationRequest ar : request.getAllocations()) {

                Invoice invoice = invoiceRepository.findById(ar.getInvoiceId())
                        .orElseThrow(() -> new RuntimeException("Invoice not found"));

                Allocation allocation = new Allocation();
                allocation.setMoneyTransaction(tx);
                allocation.setInvoice(invoice);
                allocation.setAmount(ar.getAmount());

                tx.getAllocations().add(allocation);
            }
        }

        MoneyTransaction saved =
                moneyTransactionService.createTransaction(tx);

        MoneyTransactionResponse response =
                new MoneyTransactionResponse();

        response.setId(saved.getId());
        response.setMessage("Money transaction created");

        return response;
    }
}