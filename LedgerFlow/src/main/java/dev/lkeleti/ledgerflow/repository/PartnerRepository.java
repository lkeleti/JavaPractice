package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartnerRepository
        extends JpaRepository<Partner, Long>,
        JpaSpecificationExecutor<Partner> {
}
