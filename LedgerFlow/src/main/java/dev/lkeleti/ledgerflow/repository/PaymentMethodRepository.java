package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}