package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.FinancialAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.FinancialAccountResponse;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import dev.lkeleti.ledgerflow.service.FinancialAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial-accounts")
@RequiredArgsConstructor
public class FinancialAccountController {

    private final FinancialAccountService service;

    @PostMapping
    public FinancialAccountResponse create(@RequestBody FinancialAccountCreateRequest request) {
        return map(service.create(request));
    }

    @GetMapping("/{id}")
    public FinancialAccountResponse get(@PathVariable Long id) {
        return map(service.get(id));
    }

    @GetMapping
    public List<FinancialAccountResponse> getAll() {
        return service.getAll().stream().map(this::map).toList();
    }

    @PutMapping("/{id}")
    public FinancialAccountResponse update(
            @PathVariable Long id,
            @RequestBody FinancialAccountCreateRequest request
    ) {
        return map(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private FinancialAccountResponse map(FinancialAccount fa) {

        FinancialAccountResponse r = new FinancialAccountResponse();

        r.setId(fa.getId());
        r.setName(fa.getName());
        r.setType(fa.getType());
        r.setAccountNumber(fa.getAccountNumber());
        r.setIban(fa.getIban());
        r.setSwift(fa.getSwift());

        if (fa.getGlAccount() != null) {
            r.setGlAccountId(fa.getGlAccount().getId());
            r.setGlAccountNumber(fa.getGlAccount().getNumber());
        }

        r.setActive(fa.isActive());

        return r;
    }
}