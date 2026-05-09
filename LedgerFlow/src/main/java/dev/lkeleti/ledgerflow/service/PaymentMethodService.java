package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.PaymentMethodCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.PaymentMethodResponse;
import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.PaymentMethodMapper;
import dev.lkeleti.ledgerflow.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    @Transactional
    public PaymentMethodResponse create(PaymentMethodCreateRequest request) {

        if (repository.existsByCode(request.getCode())) {
            throw new BusinessValidationException(ErrorMessage.PAYMENT_METHOD_ALREADY_EXISTS);
        }

        PaymentMethod pm = new PaymentMethod();

        pm.setName(request.getName());
        pm.setCode(request.getCode());
        pm.setFinancial(request.isFinancial());
        pm.setCash(request.isCash());
        pm.setActive(true);

        return mapper.toResponse(repository.save(pm));
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse get(Long id) {

        PaymentMethod pm = repository.findById(id)
                .filter(PaymentMethod::isActive)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.PAYMENT_METHOD_NOT_FOUND)
                );

        return mapper.toResponse(pm);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAll() {
        return repository.findAll()
                .stream()
                .filter(PaymentMethod::isActive)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllIncludingDeleted() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public PaymentMethodResponse update(Long id, PaymentMethodCreateRequest request) {

        PaymentMethod pm = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.PAYMENT_METHOD_NOT_FOUND)
                );

        pm.setName(request.getName());
        pm.setCode(request.getCode());
        pm.setFinancial(request.isFinancial());
        pm.setCash(request.isCash());

        return mapper.toResponse(repository.save(pm));
    }

    @Transactional
    public void delete(Long id) {

        PaymentMethod pm = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.PAYMENT_METHOD_NOT_FOUND)
                );

        pm.setActive(false);
        repository.save(pm);
    }

    @Transactional
    public PaymentMethodResponse restore(Long id) {

        PaymentMethod pm = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.PAYMENT_METHOD_NOT_FOUND)
                );

        pm.setActive(true);
        return mapper.toResponse(repository.save(pm));
    }
}
