package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    PARTNER_NOT_FOUND("PARTNER_NOT_FOUND", "Partner not found"),
    CUSTOMER_ACCOUNT_NOT_FOUND("CUSTOMER_ACCOUNT_NOT_FOUND","Customer account not found"),
    SUPPLIER_ACCOUNT_NOT_FOUND("SUPPLIER_ACCOUNT_NOT_FOUND","Supplier account not found"),

    ACCOUNTING_CONFIG_NOT_FOUND("ACCOUNTING_CONFIG_NOT_FOUND", "Könyvelési beállítás nem található"),
    ACCOUNTING_CONFIG_ALREADY_EXISTS("ACCOUNTING_CONFIG_ALREADY_EXISTS", "Már létezik könyvelési beállítás, új nem hozható létre"),
    GL_ACCOUNT_NOT_FOUND("GL_ACCOUNT_NOT_FOUND", "A megadott főkönyvi számla nem található"),

    FINANCIAL_ACCOUNT_NOT_FOUND("FINANCIAL_ACCOUNT_NOT_FOUND", "A pénzügyi számla nem található"),

    GL_ACCOUNT_ALREADY_EXISTS("GL_ACCOUNT_ALREADY_EXISTS", "A főkönyvi számla már létezik"),

    MONEY_TRANSACTION_NOT_FOUND("MONEY_TRANSACTION_NOT_FOUND", "A pénzügyi tranzakció nem található"),
    ALLOCATION_NOT_FOUND("ALLOCATION_NOT_FOUND", "Az allokáció nem található"),
    INVOICE_NOT_FOUND("INVOICE_NOT_FOUND", "A számla nem található"),


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
