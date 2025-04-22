package dev.lkeleti.invotraxapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SerialNumberDetail {
    private Long id;
    private String serial;
    private boolean used = false;
    private Product product;
    private InventoryItem inventoryItem;
    private LocalDate receivedAt;
    private LocalDate soldAt;
    private Integer warrantyMonths;

    public LocalDate getWarrantyEndDate() {
        return soldAt != null ? soldAt.plusMonths(warrantyMonths) : null;
    }
}
