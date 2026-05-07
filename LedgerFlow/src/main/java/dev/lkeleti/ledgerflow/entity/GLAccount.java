package dev.lkeleti.ledgerflow.entity;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gl_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GLAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // pl. 311, 454, 911
    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GLAccountType type;

    // ÁFA-hoz kapcsolódik-e
    private boolean vatRelated;

    // partner kartonhoz kapcsolódik-e
    private boolean customerRelated;

    private boolean supplierRelated;

    // könyvelhető-e közvetlenül
    private boolean bookable = true;

    // aktív-e
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}