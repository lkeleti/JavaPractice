package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Page<Inventory> findByIdContainingIgnoreCase(Long id, Pageable pageable);
}