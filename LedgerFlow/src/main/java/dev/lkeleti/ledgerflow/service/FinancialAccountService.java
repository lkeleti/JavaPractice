package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.FinancialAccountCreateRequest;
import dev.lkeleti.ledgerflow.entity.FinancialAccount;
import dev.lkeleti.ledgerflow.entity.GLAccount;
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

    @Transactional
    public FinancialAccount create(FinancialAccountCreateRequest request) {

        FinancialAccount fa = new FinancialAccount();

        fa.setName(request.getName());
        fa.setType(request.getType());
        fa.setAccountNumber(request.getAccountNumber());
        fa.setIban(request.getIban());
        fa.setSwift(request.getSwift());

        GLAccount gl = glAccountRepository.findById(request.getGlAccountId())
                .orElseThrow(() -> new RuntimeException("GLAccount not found"));

        fa.setGlAccount(gl);

        return repository.save(fa);
    }

    @Transactional(readOnly = true)
    public FinancialAccount get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("FinancialAccount not found"));
    }

    @Transactional(readOnly = true)
    public List<FinancialAccount> getAll() {
        return repository.findAll();
    }

    @Transactional
    public FinancialAccount update(Long id, FinancialAccountCreateRequest request) {

        FinancialAccount fa = get(id);

        fa.setName(request.getName());
        fa.setType(request.getType());
        fa.setAccountNumber(request.getAccountNumber());
        fa.setIban(request.getIban());
        fa.setSwift(request.getSwift());

        GLAccount gl = glAccountRepository.findById(request.getGlAccountId())
                .orElseThrow(() -> new RuntimeException("GLAccount not found"));

        fa.setGlAccount(gl);

        return repository.save(fa);
    }

    @Transactional
    public void delete(Long id) {

        FinancialAccount fa = get(id);
        fa.setActive(false);
        repository.save(fa);
    }
}