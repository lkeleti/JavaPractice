package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

}