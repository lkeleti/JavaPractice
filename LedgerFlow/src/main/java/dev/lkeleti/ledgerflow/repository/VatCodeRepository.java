package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.VatCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VatCodeRepository extends JpaRepository<VatCode, Long> {
}
