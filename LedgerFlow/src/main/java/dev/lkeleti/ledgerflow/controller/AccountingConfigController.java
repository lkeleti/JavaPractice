package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.AccountingConfigRequest;
import dev.lkeleti.ledgerflow.dto.response.AccountingConfigResponse;
import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.service.AccountingConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounting-config")
@RequiredArgsConstructor
public class AccountingConfigController {

    private final AccountingConfigService service;

    @GetMapping
    public AccountingConfigResponse get() {

        AccountingConfig config = service.get();

        if (config == null) {
            return null;
        }

        return map(config);
    }

    @PostMapping
    public AccountingConfigResponse save(
            @RequestBody AccountingConfigRequest request
    ) {

        return map(service.save(request));
    }

    @PutMapping
    public AccountingConfigResponse update(
            @RequestBody AccountingConfigRequest request
    ) {

        return map(service.save(request));
    }

    // =========================
    // MAPPER
    // =========================

    private AccountingConfigResponse map(
            AccountingConfig config
    ) {

        AccountingConfigResponse response =
                new AccountingConfigResponse();

        response.setId(config.getId());

        mapAccount(
                config.getRevenueAccount(),
                response,
                "revenue"
        );

        mapAccount(
                config.getExpenseAccount(),
                response,
                "expense"
        );

        mapAccount(
                config.getVatPayableAccount(),
                response,
                "vatPayable"
        );

        mapAccount(
                config.getVatReceivableAccount(),
                response,
                "vatReceivable"
        );

        return response;
    }

    private void mapAccount(
            GLAccount account,
            AccountingConfigResponse response,
            String type
    ) {

        if (account == null) {
            return;
        }

        switch (type) {

            case "revenue" -> {
                response.setRevenueAccountId(account.getId());
                response.setRevenueAccountNumber(account.getNumber());
                response.setRevenueAccountName(account.getName());
            }

            case "expense" -> {
                response.setExpenseAccountId(account.getId());
                response.setExpenseAccountNumber(account.getNumber());
                response.setExpenseAccountName(account.getName());
            }

            case "vatPayable" -> {
                response.setVatPayableAccountId(account.getId());
                response.setVatPayableAccountNumber(account.getNumber());
                response.setVatPayableAccountName(account.getName());
            }

            case "vatReceivable" -> {
                response.setVatReceivableAccountId(account.getId());
                response.setVatReceivableAccountNumber(account.getNumber());
                response.setVatReceivableAccountName(account.getName());
            }
        }
    }
}