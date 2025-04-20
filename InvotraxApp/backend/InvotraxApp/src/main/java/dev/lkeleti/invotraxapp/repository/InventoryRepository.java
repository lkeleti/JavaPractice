package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}