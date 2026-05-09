package dev.lkeleti.ledgerflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerCreateRequest {

    @NotBlank
    private String name;

    private boolean privatePerson;

    private String postalCode;
    private String city;
    private String district;
    private String streetName;
    private String streetType;
    private String houseNumber;
    private String building;
    private String staircase;
    private String floor;
    private String door;
    private String plotNumber;

    private String taxNumber;

    private Long customerAccountId;
    private Long supplierAccountId;

    private String bankAccountNumber;
    private String iban;
    private String swift;

    private Long paymentMethodId;

    private Integer paymentDeadlineDays;

    private String email;
    private String phone;

    private String note;

}