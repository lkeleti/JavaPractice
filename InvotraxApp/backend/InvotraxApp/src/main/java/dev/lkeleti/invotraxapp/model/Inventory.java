package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Partner supplier;

    @Column(name = "received_at")
    private LocalDate receivedAt;

    @Column(name = "invoice_number")
    private String invoiceNUmber;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryItem> items;

    public Inventory(Partner supplier, LocalDate receivedAt, String invoiceNUmber) {
        this.supplier = supplier;
        this.receivedAt = receivedAt;
        this.invoiceNUmber = invoiceNUmber;
        this.items = new ArrayList<>();
    }
}
