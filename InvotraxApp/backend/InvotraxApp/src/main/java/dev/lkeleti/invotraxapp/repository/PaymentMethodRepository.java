package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

}
