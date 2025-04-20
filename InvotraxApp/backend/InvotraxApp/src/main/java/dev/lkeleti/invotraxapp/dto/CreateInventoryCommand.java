package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateInventoryCommand {
    private Long supplierId;
    private LocalDate receivedAt;
    private String invoiceNUmber;
}
