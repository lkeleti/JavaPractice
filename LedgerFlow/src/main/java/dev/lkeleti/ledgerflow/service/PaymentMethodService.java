package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.PaymentMethodCreateRequest;
import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import dev.lkeleti.ledgerflow.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    @Transactional
    public PaymentMethod create(PaymentMethodCreateRequest request) {

        if (repository.existsByCode(request.getCode())) {
            throw new RuntimeException("PaymentMethod code already exists");
        }

        PaymentMethod pm = new PaymentMethod();

        pm.setName(request.getName());
        pm.setCode(request.getCode());
        pm.setFinancial(request.isFinancial());
        pm.setCash(request.isCash());

        return repository.save(pm);
    }

    @Transactional(readOnly = true)
    public PaymentMethod get(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found"));
    }

    @Transactional(readOnly = true)
    public List<PaymentMethod> getAll() {
        return repository.findAll();
    }

    @Transactional
    public PaymentMethod update(Long id, PaymentMethodCreateRequest request) {

        PaymentMethod pm = get(id);

        pm.setName(request.getName());
        pm.setCode(request.getCode());
        pm.setFinancial(request.isFinancial());
        pm.setCash(request.isCash());

        return repository.save(pm);
    }

    @Transactional
    public void delete(Long id) {

        PaymentMethod pm = get(id);

        pm.setActive(false);

        repository.save(pm);
    }
}