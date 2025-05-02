package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findByDeletedIsFalse();

    @Query("SELECT p FROM Partner p JOIN p.zipCode z LEFT JOIN p.preferredPaymentMethod m WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.taxNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Partner> searchByNameOrTaxNumber(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("""
    SELECT p FROM Partner p
    JOIN p.zipCode z
    LEFT JOIN p.preferredPaymentMethod m
    WHERE p.id NOT IN (SELECT s.partner.id FROM SellerCompanyProfile s)
      AND (
        LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
        LOWER(p.taxNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
      )""")
    Page<Partner> searchByNameOrTaxNumberExcludingSeller(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Partner p WHERE p.id NOT IN (SELECT s.partner.id FROM SellerCompanyProfile s)")
    Page<Partner> findAllExcludingSeller(Pageable pageable);

    Optional<Partner> findByTaxNumber(String taxNumber);
}