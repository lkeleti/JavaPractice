package dev.lkeleti.invotraxapp.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponseDto errorDetails = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponseDto errorDetails = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(), // "Conflict"
                "Database constraint violation occurred. Check unique fields or foreign key relations.",
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // 409
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDto errorDetails = new ErrorResponseDto(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorDetails, status);
    }
}