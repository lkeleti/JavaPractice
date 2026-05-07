package dev.lkeleti.ledgerflow.dto.request;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLAccountCreateRequest {

    @NotBlank
    private String number;

    @NotBlank
    private String name;

    @NotNull
    private GLAccountType type;

    private boolean vatRelated;

    private boolean customerRelated;

    private boolean supplierRelated;

    private boolean bookable = true;

    private boolean active = true;
}