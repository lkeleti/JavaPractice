package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.status IN ('NYITOTT', 'RESZBEN_FIZETETT')
        """) List<Invoice> findOpenInvoices();

    List<Invoice> findByPartnerId(Long partnerId);
}