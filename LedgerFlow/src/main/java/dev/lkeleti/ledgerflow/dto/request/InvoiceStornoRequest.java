package dev.lkeleti.ledgerflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceStornoRequest {

    @Schema(description = "A sztornó számla kiállítási dátuma", example = "2024-05-10")
    private String issueDate;
}
