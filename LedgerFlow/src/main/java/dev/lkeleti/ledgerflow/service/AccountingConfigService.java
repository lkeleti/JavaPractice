package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.AccountingConfigRequest;
import dev.lkeleti.ledgerflow.dto.response.AccountingConfigResponse;
import dev.lkeleti.ledgerflow.entity.AccountingConfig;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.AccountingConfigMapper;
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
    private final AccountingConfigMapper mapper;

    @Transactional(readOnly = true)
    public AccountingConfigResponse get() {

        AccountingConfig config = repository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.ACCOUNTING_CONFIG_NOT_FOUND)
                );

        return mapper.toResponse(config);
    }

    @Transactional
    public AccountingConfigResponse create(AccountingConfigRequest request) {

        boolean exists = repository.findAll()
                .stream()
                .findFirst()
                .isPresent();

        if (exists) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_CONFIG_ALREADY_EXISTS);
        }

        AccountingConfig config = new AccountingConfig();
        applyRequestToConfig(request, config);

        return mapper.toResponse(repository.save(config));
    }

    @Transactional
    public AccountingConfigResponse update(AccountingConfigRequest request) {

        AccountingConfig config = repository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.ACCOUNTING_CONFIG_NOT_FOUND)
                );

        applyRequestToConfig(request, config);

        return mapper.toResponse(repository.save(config));
    }

    // =========================
    // HELPERS
    // =========================

    private void applyRequestToConfig(AccountingConfigRequest request, AccountingConfig config) {

        config.setRevenueAccount(getAccount(request.getRevenueAccountId()));
        config.setExpenseAccount(getAccount(request.getExpenseAccountId()));
        config.setVatPayableAccount(getAccount(request.getVatPayableAccountId()));
        config.setVatReceivableAccount(getAccount(request.getVatReceivableAccountId()));
    }

    private GLAccount getAccount(Long id) {

        if (id == null) {
            return null;
        }

        return glAccountRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                );
    }
}
