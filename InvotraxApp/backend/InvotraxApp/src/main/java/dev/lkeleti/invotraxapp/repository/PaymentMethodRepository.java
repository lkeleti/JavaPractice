package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByCode(String code);
}
