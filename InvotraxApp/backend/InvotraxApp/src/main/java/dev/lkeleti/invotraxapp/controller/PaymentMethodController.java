package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.dto.PaymentMethodDto;
import dev.lkeleti.invotraxapp.dto.UpdatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.service.PaymentMethodService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentmethods")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class PaymentMethodController {
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethodDto> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

    @GetMapping("/{id}")
    public PaymentMethodDto getPaymentMethodById(@PathVariable Long id) {
        return paymentMethodService.getPaymentMethodById(id);
    }

    @PutMapping("/{id}")
    public PaymentMethodDto updatePaymentMethod(@PathVariable Long id, @RequestBody UpdatePaymentMethodCommand command) {
        return paymentMethodService.updatePaymentMethod(id, command);
    }

    @PostMapping
    public PaymentMethodDto createPaymentMethod(@RequestBody CreatePaymentMethodCommand command) {
        return paymentMethodService.createPaymentMethod(command);
    }
}
