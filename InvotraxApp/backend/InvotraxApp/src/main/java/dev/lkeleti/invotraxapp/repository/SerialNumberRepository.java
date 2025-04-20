package dev.lkeleti.invotraxapp.repository;

import dev.lkeleti.invotraxapp.model.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {

}