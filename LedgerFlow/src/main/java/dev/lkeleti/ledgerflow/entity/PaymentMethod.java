package dev.lkeleti.ledgerflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // pl: Átutalás
    @Column(nullable = false, unique = true)
    private String name;

    // pl: TRANSFER
    @Column(nullable = false, unique = true)
    private String code;

    // pénzmozgást generál-e
    private boolean financial = true;

    // készpénzes-e
    private boolean cash;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}