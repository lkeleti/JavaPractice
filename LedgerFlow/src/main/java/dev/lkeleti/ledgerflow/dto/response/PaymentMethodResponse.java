package dev.lkeleti.ledgerflow.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodResponse {

    private Long id;

    private String name;

    private String code;

    private boolean financial;

    private boolean cash;

    private boolean active;
}