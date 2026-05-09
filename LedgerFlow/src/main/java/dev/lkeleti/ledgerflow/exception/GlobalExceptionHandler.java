package dev.lkeleti.ledgerflow.exception;

import dev.lkeleti.ledgerflow.dto.response.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------
    // NOT FOUND
    // -------------------------------
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getErrorMessage().getCode(),
                ex.getErrorMessage().getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // -------------------------------
    // BUSINESS VALIDATION
    // -------------------------------
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessValidationException ex) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrorMessage().getCode(),
                ex.getErrorMessage().getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(error);
    }

    // -------------------------------
    // BEAN VALIDATION (DTO mezők)
    // -------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessage.VALIDATION_ERROR.getCode(),
                ErrorMessage.VALIDATION_ERROR.getMessage(),
                details
        );

        return ResponseEntity.badRequest().body(error);
    }

    // -------------------------------
    // PATHVARIABLE / REQUESTPARAM VALIDATION
    // -------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {

        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessage.CONSTRAINT_VIOLATION.getCode(),
                ErrorMessage.CONSTRAINT_VIOLATION.getMessage(),
                details
        );

        return ResponseEntity.badRequest().body(error);
    }

    // -------------------------------
    // ILLEGAL ARGUMENT
    // -------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorMessage.ILLEGAL_ARGUMENT.getCode(),
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(error);
    }

    // -------------------------------
    // GENERAL FALLBACK
    // -------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {

        ApiError error = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorMessage.INTERNAL_ERROR.getCode(),
                ErrorMessage.INTERNAL_ERROR.getMessage(),
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
