package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

}