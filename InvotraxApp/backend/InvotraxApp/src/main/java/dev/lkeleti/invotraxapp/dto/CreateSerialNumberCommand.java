package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateSerialNumberCommand {
    private String serial;
    private boolean used;
    private Long productId;
    private Long inventoryItemId;
    private Long invoiceItemId;
}
