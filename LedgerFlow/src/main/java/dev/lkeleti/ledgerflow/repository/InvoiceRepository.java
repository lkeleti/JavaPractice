package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}