package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryDto {
    private Long id;
    private Long supplierId;
    private LocalDate receivedAt;
    private String invoiceNumber;
    private List<InventoryItemDto> items;
}
