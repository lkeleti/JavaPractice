package dev.lkeleti.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="Kölcsönzés azonosítója", example = "1")
    @NotNull(message = "Book ID cannot be blank")
    private Long bookId;

    @Schema(description="Kölcsönző neve", example = "John Doe")
    @NotBlank(message = "Borrower's name name cannot be blank")
    private String borrowerName;
}
