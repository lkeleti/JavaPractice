package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    PARTNER_NOT_FOUND("PARTNER_NOT_FOUND", "Partner nem található"),
    CUSTOMER_ACCOUNT_NOT_FOUND("CUSTOMER_ACCOUNT_NOT_FOUND","Vevő főkönyvi számla nem található"),
    SUPPLIER_ACCOUNT_NOT_FOUND("SUPPLIER_ACCOUNT_NOT_FOUND","Szállító főkönyvi számla nem található"),

    ACCOUNTING_CONFIG_NOT_FOUND("ACCOUNTING_CONFIG_NOT_FOUND", "Könyvelési beállítás nem található"),
    ACCOUNTING_CONFIG_ALREADY_EXISTS("ACCOUNTING_CONFIG_ALREADY_EXISTS", "Már létezik könyvelési beállítás, új nem hozható létre"),

    GL_ACCOUNT_NOT_FOUND("GL_ACCOUNT_NOT_FOUND", "A megadott főkönyvi számla nem található"),
    GL_ACCOUNT_ALREADY_EXISTS("GL_ACCOUNT_ALREADY_EXISTS", "A főkönyvi számla már létezik"),

    FINANCIAL_ACCOUNT_NOT_FOUND("FINANCIAL_ACCOUNT_NOT_FOUND", "A pénzügyi számla nem található"),

    MONEY_TRANSACTION_NOT_FOUND("MONEY_TRANSACTION_NOT_FOUND", "A pénzügyi tranzakció nem található"),
    ALLOCATION_NOT_FOUND("ALLOCATION_NOT_FOUND", "Az allokáció nem található"),
    INVOICE_NOT_FOUND("INVOICE_NOT_FOUND", "A számla nem található"),

    PAYMENT_METHOD_NOT_FOUND("PAYMENT_METHOD_NOT_FOUND", "A fizetési mód nem található"),
    PAYMENT_METHOD_ALREADY_EXISTS("PAYMENT_METHOD_ALREADY_EXISTS", "A fizetési mód kódja már létezik"),

    VAT_CODE_NOT_FOUND("VAT_CODE_NOT_FOUND", "Az ÁFA kód nem található"),
    VAT_CODE_ALREADY_EXISTS("VAT_CODE_ALREADY_EXISTS", "Az ÁFA kód már létezik"),

    VALIDATION_ERROR("VALIDATION_ERROR", "Érvényességi hiba"),
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "Paraméter érvényességi hiba"),
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "Érvénytelen argumentum"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Váratlan hiba történt"),

    BUSINESS_ERROR("BUSINESS_ERROR", "Üzleti validációs hiba");

    private final String code;
    private final String message;

    ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
