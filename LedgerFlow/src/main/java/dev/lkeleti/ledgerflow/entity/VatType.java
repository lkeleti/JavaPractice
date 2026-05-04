package dev.lkeleti.ledgerflow.entity;

public enum VatType {
    NORMAL,        // normál áfa
    EXEMPT,        // tárgyi mentes
    REVERSE,       // fordított
    OUT_OF_SCOPE   // hatályon kívüli
}