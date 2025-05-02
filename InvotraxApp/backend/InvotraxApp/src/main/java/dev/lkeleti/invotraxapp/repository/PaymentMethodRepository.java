package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.PaymentMethod;
import dev.lkeleti.invotraxapp.model.ZipCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<ZipCode> findByDeletedIsFalse();
}
