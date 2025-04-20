package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

}