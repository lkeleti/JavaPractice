package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CreateInvoiceNumberSequenceCommand {
    private Long invoiceTypeId;
    private String invoicePrefix;
    private int lastNumber;
}
