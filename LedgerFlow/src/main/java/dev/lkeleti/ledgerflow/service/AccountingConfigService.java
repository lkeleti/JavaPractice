package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.AccountingConfigRequest;
import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.repository.AccountingConfigRepository;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountingConfigService {

    private final AccountingConfigRepository repository;
    private final GLAccountRepository glAccountRepository;

    @Transactional(readOnly = true)
    public AccountingConfig get() {

        return repository.findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public AccountingConfig save(
            AccountingConfigRequest request
    ) {

        AccountingConfig config = repository.findAll()
                .stream()
                .findFirst()
                .orElse(new AccountingConfig());

        config.setRevenueAccount(
                getAccount(request.getRevenueAccountId())
        );

        config.setExpenseAccount(
                getAccount(request.getExpenseAccountId())
        );

        config.setVatPayableAccount(
                getAccount(request.getVatPayableAccountId())
        );

        config.setVatReceivableAccount(
                getAccount(request.getVatReceivableAccountId())
        );

        return repository.save(config);
    }

    // =========================
    // HELPERS
    // =========================

    private GLAccount getAccount(Long id) {

        if (id == null) {
            return null;
        }

        return glAccountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "GL account not found: " + id
                        ));
    }
}