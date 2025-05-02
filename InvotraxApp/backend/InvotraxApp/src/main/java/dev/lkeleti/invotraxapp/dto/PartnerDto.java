package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PartnerDto {
    private Long id;
    private String name;
    private ZipCodeDto zipCode;
    private String streetName;
    private String streetType;
    private String houseNumber;
    private String building;
    private String staircase;
    private String floor;
    private String door;
    private String landRegistryNumber;
    private boolean isPrivate;
    private String taxNumber;
    private String email;
    private String phoneNumber;
    private PaymentMethodDto preferredPaymentMethod;
    private LocalDateTime createdAt;
    private BigDecimal balance;
    private Integer defaultPaymentDeadline;
    private String bankName;
    private String bankNumber;
    private String iban;
    private boolean deleted;
}
