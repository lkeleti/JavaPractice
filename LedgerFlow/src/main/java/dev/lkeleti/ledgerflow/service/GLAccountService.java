package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.GLAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.GLAccountUpdateRequest;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GLAccountService {

    private final GLAccountRepository repository;

    @Transactional
    public GLAccount create(
            GLAccountCreateRequest request
    ) {

        if (repository.existsByNumber(request.getNumber())) {
            throw new IllegalStateException(
                    "GL account already exists"
            );
        }

        GLAccount account = new GLAccount();

        map(request, account);

        return repository.save(account);
    }

    @Transactional(readOnly = true)
    public GLAccount getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("GL account not found"));
    }

    @Transactional(readOnly = true)
    public List<GLAccount> getAll() {

        return repository.findAll();
    }

    @Transactional
    public GLAccount update(
            Long id,
            GLAccountUpdateRequest request
    ) {

        GLAccount account = getById(id);

        map(request, account);

        return repository.save(account);
    }

    @Transactional
    public void delete(Long id) {

        GLAccount account = getById(id);

        account.setActive(false);

        repository.save(account);
    }

    // =========================
    // MAPPER
    // =========================

    private void map(
            GLAccountCreateRequest request,
            GLAccount account
    ) {

        account.setNumber(request.getNumber());
        account.setName(request.getName());
        account.setType(request.getType());

        account.setVatRelated(
                request.isVatRelated()
        );

        account.setCustomerRelated(
                request.isCustomerRelated()
        );

        account.setSupplierRelated(
                request.isSupplierRelated()
        );

        account.setBookable(
                request.isBookable()
        );

        account.setActive(
                request.isActive()
        );
    }
}