package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

}