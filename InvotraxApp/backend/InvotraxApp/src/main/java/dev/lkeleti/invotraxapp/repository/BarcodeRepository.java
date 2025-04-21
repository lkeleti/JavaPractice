package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    Optional<Barcode> findByCode(String code);

    @Query("SELECT b.code FROM Barcode b WHERE b.isGenerated = true ORDER BY b.code DESC LIMIT 1")
    Optional<String> findHighestGeneratedBarcode();
}