package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InvoiceNumberSequence;
import dev.lkeleti.invotraxapp.model.InvoiceType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceNumberSequenceRepository extends JpaRepository<InvoiceNumberSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceNumberSequence s WHERE s.invoiceType = :invoiceType")
    InvoiceNumberSequence lockByInvoiceType(@Param("invoiceType") String invoiceType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    InvoiceNumberSequence findByInvoiceType(InvoiceType invoiceType);
}