package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.GLAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.GLAccountUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.GLAccountResponse;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.GLAccountMapper;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GLAccountService {

    private final GLAccountRepository repository;
    private final GLAccountMapper mapper;

    @Transactional
    public GLAccountResponse create(GLAccountCreateRequest request) {

        if (repository.existsByNumber(request.getNumber())) {
            throw new BusinessValidationException(ErrorMessage.GL_ACCOUNT_ALREADY_EXISTS);
        }

        GLAccount account = new GLAccount();
        applyRequest(account, request);

        return mapper.toResponse(repository.save(account));
    }

    @Transactional(readOnly = true)
    public GLAccountResponse getById(Long id) {

        GLAccount account = repository.findById(id)
                .filter(GLAccount::isActive)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                );

        return mapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<GLAccountResponse> getAll() {
        return repository.findAll()
                .stream()
                .filter(GLAccount::isActive)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GLAccountResponse> getAllIncludingDeleted() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public GLAccountResponse update(Long id, GLAccountUpdateRequest request) {

        GLAccount account = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                );

        applyRequest(account, request);

        return mapper.toResponse(repository.save(account));
    }

    @Transactional
    public void delete(Long id) {

        GLAccount account = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                );

        account.setActive(false);
        repository.save(account);
    }

    @Transactional
    public GLAccountResponse restore(Long id) {

        GLAccount account = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.GL_ACCOUNT_NOT_FOUND)
                );

        account.setActive(true);
        return mapper.toResponse(repository.save(account));
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------
    private void applyRequest(GLAccount account, GLAccountCreateRequest request) {
        account.setNumber(request.getNumber());
        account.setName(request.getName());
        account.setType(request.getType());
        account.setVatRelated(request.isVatRelated());
        account.setCustomerRelated(request.isCustomerRelated());
        account.setSupplierRelated(request.isSupplierRelated());
        account.setBookable(request.isBookable());
        account.setActive(request.isActive());
    }

    private void applyRequest(GLAccount account, GLAccountUpdateRequest request) {
        account.setNumber(request.getNumber());
        account.setName(request.getName());
        account.setType(request.getType());
        account.setVatRelated(request.isVatRelated());
        account.setCustomerRelated(request.isCustomerRelated());
        account.setSupplierRelated(request.isSupplierRelated());
        account.setBookable(request.isBookable());
        account.setActive(request.isActive());
    }
}
