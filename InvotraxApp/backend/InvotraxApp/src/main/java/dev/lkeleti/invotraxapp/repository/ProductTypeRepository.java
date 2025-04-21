package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository  extends JpaRepository<ProductType, Long> {
}
