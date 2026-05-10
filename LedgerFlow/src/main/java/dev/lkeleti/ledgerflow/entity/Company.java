package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cégnév
    @Column(nullable = false)
    private String name;

    // Adószám
    private String taxNumber;

    // Székhely
    private String postalCode;
    private String city;
    private String streetName;
    private String streetType;
    private String houseNumber;

    // Könyvelési zárás dátuma
    @Column(nullable = false)
    private LocalDate closedAccountingPeriod = LocalDate.of(1900, 1, 1);

    // Létrehozás ideje
    @CreationTimestamp
    private LocalDateTime createdAt;
}
