package dev.lkeleti.ledgerflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PartnerResponse {

    @Schema(description = "A partner azonosítója", example = "1")
    private Long id;

    @Schema(description = "A partner neve", example = "Kovács és Társa Kft.")
    private String name;

    @Schema(description = "Magánszemély", example = "igaz/hamis")
    private boolean privatePerson;

    @Schema(description = "A partner irányítószáma", example = "5000")
    private String postalCode;

    @Schema(description = "A partner települése", example = "Szolnok")
    @NotBlank(message = "A település megadása kötelező")
    private String city;

    @Schema(description = "Kerület", example = "XI. kerület")
    private String district;

    @Schema(description = "Utcanév", example = "Kossuth Lajos")
    private String streetName;

    @Schema(description = "Közterület típusa", example = "utca")
    private String streetType;

    @Schema(description = "Ház szám", example = "15.")
    private String houseNumber;

    @Schema(description = "Épület", example = "A. épület")
    private String building;

    @Schema(description = "Lépcsőház", example = "A. lépcsőház")
    private String staircase;

    @Schema(description = "Emelet", example = "2")
    private String floor;

    @Schema(description = "ajtó", example = "3")
    private String door;

    @Schema(description = "Helyrajzi szám", example = "1234/12")
    private String plotNumber;

    @Schema(description = "Adószám", example = "12345678-1-16")
    private String taxNumber;

    @Schema(description = "Vevő számlaszám azonosítója", example = "2")
    private Long customerAccountId;

    @Schema(description = "Eladó számlaszám azonosítója", example = "2")
    private Long supplierAccountId;

    @Schema(description = "Bankszámla száma", example = "11111111-00000000-22222222")
    private String bankAccountNumber;

    @Schema(description = "IBAN szám", example = "HU-123456789")
    private String iban;

    @Schema(description = "Swfit kód", example = "OTPVHUHB")
    private String swift;

    @Schema(description = "Preferált fizetési mód azonosítója", example = "2")
    private Long paymentMethodId;

    @Schema(description = "Fizetési határidő napban", example = "8")
    private Integer paymentDeadlineDays;

    @Schema(description = "Email cím", example = "valaki@email.com")
    private String email;

    @Schema(description = "Telefonszám", example = "+36/1/123-1234")
    private String phone;

    @Schema(description = "Megjegyzés", example = "Legjobb vásárló!")
    private String note;

    @Schema(description = "Rögzítés időpontja", example = "2026.05.09 10:40")
    private LocalDateTime createdAt;

    @Schema(description = "Törölt-e?", example = "igaz/hamis")
    private boolean deleted;
}