package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query(value = "SELECT * FROM inventory i " +
            "JOIN partner s ON i.supplier_id = s.id " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(s.tax_number) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR DATE_FORMAT(i.received_at, '%Y.%m.%d') LIKE :searchTerm " +
            "OR LOWER(i.invoice_number) LIKE LOWER(CONCAT('%', :searchTerm, '%'))",
            nativeQuery = true)
    Page<Inventory> searchBySupplierNameOrSupplierTaxOrReceivedAtOrInvoiceNumber(@Param("searchTerm") String searchTerm, Pageable pageable);


    @Query("SELECT i FROM Inventory i " +
            "LEFT JOIN FETCH i.supplier " +
            "LEFT JOIN FETCH i.items items " +
            "LEFT JOIN FETCH items.product " +
            "WHERE i.id = :id")
    Optional<Inventory> findByIdWithAllData(@Param("id") Long id);
}