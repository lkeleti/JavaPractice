package dev.lkeleti.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanDto {
    @Schema(description="Kölcsönzés azonosítója", example = "1")
    private Long id;
    @Schema(description="Kölcsönző neve", example = "John Doe")
    private String borrowerName;
    @Schema(description="Kölcsönzés dátuma", example = "2025.04.06")
    private LocalDate loanDate;
    @Schema(description="Kölcsönzés lejáratának dátuma", example = "2025.04.20")
    private LocalDate dueDate;
    @Schema(description="Könyv visszahozatalának dátuma", example = "2025.04.13")
    private LocalDate returnDate;
    @Schema(description="Könyv entitás")
    private BookDto book;
}
