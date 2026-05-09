package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findAllByDeletedFalse();
}