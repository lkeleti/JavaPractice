package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.InvoiceVatSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceVatSummaryRepository extends JpaRepository<InvoiceVatSummary, Long> {
}