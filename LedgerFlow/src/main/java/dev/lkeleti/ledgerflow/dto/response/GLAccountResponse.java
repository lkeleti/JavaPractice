package dev.lkeleti.ledgerflow.dto.response;

import dev.lkeleti.ledgerflow.entity.enums.GLAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLAccountResponse {

    private Long id;

    private String number;

    private String name;

    private GLAccountType type;

    private boolean vatRelated;

    private boolean customerRelated;

    private boolean supplierRelated;

    private boolean bookable;

    private boolean active;
}