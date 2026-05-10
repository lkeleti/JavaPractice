package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    // ===== PARTNER =====
    PARTNER_NOT_FOUND("PARTNER_NOT_FOUND", "Partner nem található"),
    CUSTOMER_ACCOUNT_NOT_FOUND("CUSTOMER_ACCOUNT_NOT_FOUND","Vevő főkönyvi számla nem található"),
    SUPPLIER_ACCOUNT_NOT_FOUND("SUPPLIER_ACCOUNT_NOT_FOUND","Szállító főkönyvi számla nem található"),

    // ===== ACCOUNTING CONFIG =====
    ACCOUNTING_CONFIG_NOT_FOUND("ACCOUNTING_CONFIG_NOT_FOUND", "Könyvelési beállítás nem található"),
    ACCOUNTING_CONFIG_ALREADY_EXISTS("ACCOUNTING_CONFIG_ALREADY_EXISTS", "Már létezik könyvelési beállítás, új nem hozható létre"),

    // ===== GL ACCOUNT =====
    GL_ACCOUNT_NOT_FOUND("GL_ACCOUNT_NOT_FOUND", "A megadott főkönyvi számla nem található"),
    GL_ACCOUNT_ALREADY_EXISTS("GL_ACCOUNT_ALREADY_EXISTS", "A főkönyvi számla már létezik"),

    // ===== FINANCIAL ACCOUNT =====
    FINANCIAL_ACCOUNT_NOT_FOUND("FINANCIAL_ACCOUNT_NOT_FOUND", "A pénzügyi számla nem található"),

    // ===== MONEY TRANSACTION =====
    MONEY_TRANSACTION_NOT_FOUND("MONEY_TRANSACTION_NOT_FOUND", "A pénzügyi tranzakció nem található"),
    ALLOCATION_NOT_FOUND("ALLOCATION_NOT_FOUND", "Az allokáció nem található"),

    // ===== INVOICE =====
    INVOICE_NOT_FOUND("INVOICE_NOT_FOUND", "A számla nem található"),
    INVOICE_PARTNER_REQUIRED("INVOICE_PARTNER_REQUIRED", "Számlához partner kötelező"),
    INVOICE_PARTNER_NOT_ALLOWED_FOR_RECEIPT("INVOICE_PARTNER_NOT_ALLOWED_FOR_RECEIPT", "Nyugtához nem tartozhat partner"),
    INVOICE_VAT_SUMMARY_MISSING("INVOICE_VAT_SUMMARY_MISSING", "Nincs ÁFA bontás"),
    INVOICE_VAT_SUMMARY_INCOMPLETE("INVOICE_VAT_SUMMARY_INCOMPLETE", "Hiányos ÁFA bontás"),
    INVOICE_VAT_NEGATIVE("INVOICE_VAT_NEGATIVE", "Negatív ÁFA adat nem megengedett"),
    INVOICE_OVERPAID("INVOICE_OVERPAID", "Túlfizetés történt"),

    // ===== PAYMENT METHOD =====
    PAYMENT_METHOD_NOT_FOUND("PAYMENT_METHOD_NOT_FOUND", "A fizetési mód nem található"),
    PAYMENT_METHOD_ALREADY_EXISTS("PAYMENT_METHOD_ALREADY_EXISTS", "A fizetési mód kódja már létezik"),


    // ===== VAT CODE =====
    VAT_CODE_NOT_FOUND("VAT_CODE_NOT_FOUND", "Az ÁFA kód nem található"),
    VAT_CODE_ALREADY_EXISTS("VAT_CODE_ALREADY_EXISTS", "Az ÁFA kód már létezik"),
    VAT_CODE_MISSING("VAT_CODE_MISSING", "Az ÁFA kód kötelező"),
    VAT_CODE_UNKNOWN("VAT_CODE_UNKNOWN", "Ismeretlen ÁFA kulcs"),

    // ===== ACCOUNTING =====
    JOURNAL_NOT_BALANCED("JOURNAL_NOT_BALANCED", "A könyvelési tétel nem egyenlő (T ≠ K)"),

    // ===== COMPANY =====
    COMPANY_ALREADY_EXISTS("COMPANY_ALREADY_EXISTS", "Csak egy cég rögzíthető a rendszerben"),
    COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", "A cég adatai nem találhatók"),
    ACCOUNTING_PERIOD_CLOSED("ACCOUNTING_PERIOD_CLOSED", "A könyvelési időszak lezárva, módosítás nem engedélyezett"),
    ACCOUNTING_PERIOD_CANNOT_MOVE_BACK("ACCOUNTING_PERIOD_CANNOT_MOVE_BACK", "A könyvelési időszak nem állítható korábbi dátumra"),
    ACCOUNTING_PERIOD_CANNOT_MOVE_FORWARD("ACCOUNTING_PERIOD_CANNOT_MOVE_FORWARD", "Újranyitáskor a könyvelési időszak nem tolható előre"),


    // ===== COMMON =====
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
