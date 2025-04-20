package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.VatRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VatRateRepository extends JpaRepository<VatRate,Long> {
}
