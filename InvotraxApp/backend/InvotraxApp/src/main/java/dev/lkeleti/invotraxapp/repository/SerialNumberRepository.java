package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {
    Optional<SerialNumber> findBySerial(String serial);
    @Query("""
    SELECT sn FROM SerialNumber sn
    JOIN FETCH sn.inventoryItem ii
    JOIN FETCH ii.inventory i
    JOIN FETCH sn.product p
    LEFT JOIN FETCH sn.invoiceItem invItem
    LEFT JOIN FETCH invItem.invoice inv
    WHERE sn.serial = :serial
    """)
    Optional<SerialNumber> findFullInfoBySerial(@Param("serial") String serial);
}