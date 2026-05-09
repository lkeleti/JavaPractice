package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    PARTNER_NOT_FOUND("PARTNER_NOT_FOUND", "Partner not found"),
    CUSTOMER_ACCOUNT_NOT_FOUND("CUSTOMER_ACCOUNT_NOT_FOUND","Customer account not found"),
    SUPPLIER_ACCOUNT_NOT_FOUND("SUPPLIER_ACCOUNT_NOT_FOUND","Supplier account not found"),

    PAYMENT_METHOD_NOT_FOUND("PAYMENT_METHOD_NOT_FOUND", "Payment method not found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation error"),
    BUSINESS_ERROR("BUSINESS_ERROR", "Business validation error");

    private final String code;
    private final String message;

    ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
