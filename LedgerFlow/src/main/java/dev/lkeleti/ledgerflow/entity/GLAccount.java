package dev.lkeleti.ledgerflow.entity;

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
@AllArgsConstructor
@NoArgsConstructor
public class GLAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number; // lehet "311" vagy "911"

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private GLAccountType type; // ESZKÖZ, FORRÁS, KÖLTSÉG, BEVÉTEL

    private boolean vatRelated;
    private boolean customerRelated;
    private boolean supplierRelated;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}