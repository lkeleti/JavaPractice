package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.PaymentMethodCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.PaymentMethodResponse;
import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import dev.lkeleti.ledgerflow.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService service;

    @PostMapping
    public PaymentMethodResponse create(
            @RequestBody PaymentMethodCreateRequest request
    ) {
        return map(service.create(request));
    }

    @GetMapping("/{id}")
    public PaymentMethodResponse get(@PathVariable Long id) {
        return map(service.get(id));
    }

    @GetMapping
    public List<PaymentMethodResponse> getAll() {

        return service.getAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @PutMapping("/{id}")
    public PaymentMethodResponse update(
            @PathVariable Long id,
            @RequestBody PaymentMethodCreateRequest request
    ) {
        return map(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private PaymentMethodResponse map(PaymentMethod pm) {

        PaymentMethodResponse r = new PaymentMethodResponse();

        r.setId(pm.getId());
        r.setName(pm.getName());
        r.setCode(pm.getCode());
        r.setFinancial(pm.isFinancial());
        r.setCash(pm.isCash());
        r.setActive(pm.isActive());

        return r;
    }
}