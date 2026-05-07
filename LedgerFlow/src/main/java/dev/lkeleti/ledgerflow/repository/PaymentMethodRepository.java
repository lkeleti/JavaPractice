package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    Optional<PaymentMethod> findByCode(String code);

    boolean existsByCode(String code);
}