package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceTypeRepository extends JpaRepository<InvoiceType, Long> {
    Optional<InvoiceType> findByCode(String code);
}
