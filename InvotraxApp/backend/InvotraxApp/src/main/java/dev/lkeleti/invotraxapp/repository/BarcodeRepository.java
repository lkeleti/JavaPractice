package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

}