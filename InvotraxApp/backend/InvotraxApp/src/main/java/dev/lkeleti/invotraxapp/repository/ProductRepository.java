package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedIsFalse();
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findBySkuContainingIgnoreCase(String sku, Pageable pageable);
}
