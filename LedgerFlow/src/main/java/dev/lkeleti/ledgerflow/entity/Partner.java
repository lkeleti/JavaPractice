package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "partner")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private boolean privatePerson;

    private String postalCode;
    private String city;
    private String district;
    private String streetName;
    private String streetType;
    private String houseNumber;
    private String building;
    private String staircase;
    private String floor;
    private String door;
    private String plotNumber;

    private String taxNumber;

    @ManyToOne
    private GLAccount customerAccount;

    @ManyToOne
    private GLAccount supplierAccount;

    private String bankAccountNumber;
    private String iban;
    private String swift;

    @ManyToOne
    private PaymentMethod paymentMethod;

    private Integer paymentDeadlineDays = 0;

    private String email;
    private String phone;

    @Column(length = 1000)
    private String note;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean deleted = false;
}