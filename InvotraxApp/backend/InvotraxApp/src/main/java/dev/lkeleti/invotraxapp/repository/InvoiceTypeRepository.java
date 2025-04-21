package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InvoiceType;
import dev.lkeleti.invotraxapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceTypeRepository extends JpaRepository<InvoiceType, Long> {
    List<Product> findByDeletedIsFalse();
}
