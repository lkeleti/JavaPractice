package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public BusinessValidationException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
