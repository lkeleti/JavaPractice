package dev.lkeleti.invotraxapp.dto;

import dev.lkeleti.invotraxapp.model.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvoiceNumberSequenceDto {
    private Long id;
    private InvoiceType invoiceType;
    private String invoicePrefix;
    private int lastNumber;
}
