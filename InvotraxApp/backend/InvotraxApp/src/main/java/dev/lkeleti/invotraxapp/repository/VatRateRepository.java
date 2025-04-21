package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.model.VatRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VatRateRepository extends JpaRepository<VatRate,Long> {
    List<Product> findByDeletedIsFalse();
}
