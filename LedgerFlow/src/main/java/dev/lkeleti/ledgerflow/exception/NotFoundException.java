package dev.lkeleti.ledgerflow.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
