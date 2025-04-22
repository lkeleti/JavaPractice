package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

}