package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedIsFalse();
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findBySkuContainingIgnoreCase(String sku, Pageable pageable);


    @Query("SELECT p FROM Product p JOIN p.barcodes b WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchByNameOrBarCodeOrSku(@Param("searchTerm") String searchTerm, Pageable pageable);
}
