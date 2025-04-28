package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.model.ZipCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZipCodeRepository extends JpaRepository<ZipCode, Long> {
    List<Product> findByDeletedIsFalse();
    List<ZipCode> findByZipAndDeletedFalse(String zip);

    @Query("SELECT z FROM ZipCode z WHERE LOWER(z.zip) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(z.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ZipCode> searchByZipOrCity(@Param("searchTerm") String searchTerm, Pageable pageable);
}