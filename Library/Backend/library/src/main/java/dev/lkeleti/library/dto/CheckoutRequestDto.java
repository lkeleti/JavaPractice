package dev.lkeleti.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckoutRequestDto {
    @NotNull(message = "Book ID cannot be blank")
    private Long bookId;

    @NotBlank(message = "Borrower's name name cannot be blank")
    private String borrowerName;
}
