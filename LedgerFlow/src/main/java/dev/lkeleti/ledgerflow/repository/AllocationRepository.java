package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByInvoiceId(Long invoiceId);
}