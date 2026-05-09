package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.FinancialAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.FinancialAccountResponse;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.FinancialAccountMapper;
import dev.lkeleti.ledgerflow.repository.FinancialAccountRepository;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialAccountService {

    private final FinancialAccountRepository repository;
    private final GLAccountRepository glAccountRepository;
    private final FinancialAccountMapper mapper;

    @Transactional
    public FinancialAccountResponse create(FinancialAccountCreateRequest request) {

        FinancialAccount fa = new FinancialAccount();
        applyRequest(fa, request);
        fa.setActive(true);

        return mapper.toResponse(repository.save(fa));
    }

    @Transactional(readOnly = true)
    public FinancialAccountResponse get(Long id) {

        FinancialAccount fa = repository.findById(id)
                .filter(FinancialAccount::isActive)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.FINANCIAL_ACCOUNT_NOT_FOUND)
                );

        return mapper.toResponse(fa);
    }

    @Transactional(readOnly = true)
    public List<FinancialAccountResponse> getAll() {
        return repository.findAll()
                .stream()
                .filter(FinancialAccount::isActive)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FinancialAccountResponse> getAllIncludingDeleted() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public FinancialAccountResponse update(Long id, FinancialAccountCreateRequest request) {

        FinancialAccount fa = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.FINANCIAL_ACCOUNT_NOT_FOUND)
                );

        applyRequest(fa, request);

        return mapper.toResponse(repository.save(fa));
    }

    @Transactional
    public void delete(Long id) {

        FinancialAccount fa = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.FINANCIAL_ACCOUNT_NOT_FOUND)
                );

        fa.setActive(false);
        repository.save(fa);
    }

    @Transactional
    public FinancialAccountResponse restore(Long id) {

        FinancialAccount fa = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.FINANCIAL_ACCOUNT_NOT_FOUND)
                );

        fa.setActive(true);
        return mapper.toResponse(repository.save(fa));
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------
    private void applyRequest(FinancialAccount fa, FinancialAccountCreateRequest request) {

        fa.setName(request.getName());
        fa.setType(request.getType());
        fa.setAccountNumber(request.getAccountNumber());
        fa.setIban(request.getIban());
        fa.setSwift(request.getSwift());

        if (request.getGlAccountId() != null) {
            GLAccount gla = glAccountRepository.findById(request.getGlAccountId())
                    .orElseThrow(() ->
                            new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                    );
            fa.setGlAccount(gla);
        }
    }
}
