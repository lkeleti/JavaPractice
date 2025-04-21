package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.model.ZipCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZipCodeRepository extends JpaRepository<ZipCode, Long> {
    List<Product> findByDeletedIsFalse();
    List<ZipCode> findByZipAndDeletedFalse(String zip);
}