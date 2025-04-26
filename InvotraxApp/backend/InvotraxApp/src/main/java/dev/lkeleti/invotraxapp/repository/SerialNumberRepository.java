package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    List<SerialNumber> findAllByUsedTrue();
    List<SerialNumber> findAllByUsedFalse();
    boolean existsBySerial(String serial);

    List<SerialNumber> findByInventoryItemId(Long inventoryItemId);

    // Példa: Gyáriszám keresése a string alapján (ha kell a teljes entitás)
    // Optional<SerialNumber> findBySerial(String serial);

    // Példa: Teljes információ lekérdezése (a Service-ben használtad) - JPQL lehet szükséges a joinok miatt
    // @Query("SELECT sn FROM SerialNumber sn LEFT JOIN FETCH sn.product LEFT JOIN FETCH sn.inventoryItem inv LEFT JOIN FETCH inv.inventory LEFT JOIN FETCH sn.invoiceItem invc LEFT JOIN FETCH invc.invoice WHERE sn.serial = :serial")
    // Optional<SerialNumber> findFullInfoBySerial(@Param("serial") String serial);

    // @Query("SELECT s.serial FROM SerialNumber s WHERE s.serial IN :serials")
    // List<String> findExistingSerials(@Param("serials") List<String> serials);
}