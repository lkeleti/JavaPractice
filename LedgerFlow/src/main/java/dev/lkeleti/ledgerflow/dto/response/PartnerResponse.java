package dev.lkeleti.ledgerflow.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerResponse {

    private Long id;

    private String name;

    private boolean privatePerson;

    private String taxNumber;

    private String email;

    private String phone;

    private boolean deleted;
}