package dev.lkeleti.ledgerflow.repository;

import dev.lkeleti.ledgerflow.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
