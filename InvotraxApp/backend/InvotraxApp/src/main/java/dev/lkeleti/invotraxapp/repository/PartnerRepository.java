package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Page<Partner> findByNameContainingIgnoreCase(String name, Pageable pageable);
}