package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "zip_code_id")
    private ZipCode zipCode;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "street_type")
    private String streetType;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "building")
    private String building;

    @Column(name = "staircase")
    private String staircase;

    @Column(name = "floor")
    private String floor;

    @Column(name = "door")
    private String door;

    @Column(name = "land_registry_number")
    private String landRegistryNumber;

    @Column(name = "is_private")
    private boolean isPrivate;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "preferred_payment_method")
    private String preferredPaymentMethod;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_number")
    private String bankNumber;

    @Column(name = "iban")
    private String iban;


    @Column(name = "default_payment_deadline")
    private Integer defaultPaymentDeadline;

    @Column(name = "deleted")
    private boolean deleted = false;
}
