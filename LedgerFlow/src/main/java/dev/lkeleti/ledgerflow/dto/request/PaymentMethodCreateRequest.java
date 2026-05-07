package dev.lkeleti.ledgerflow.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodCreateRequest {

    private String name;

    private String code;

    private boolean financial;

    private boolean cash;
}