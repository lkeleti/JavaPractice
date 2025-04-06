package dev.lkeleti.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBookCommand {

    @Schema(description="Könyv ISBN száma", example = "11111111111111111111")
    @NotBlank(message = "Book ISBN cannot be blank")
    private String isbn;

    @Schema(description="Könyv címe", example = "Egri csillagok")
    @NotBlank(message = "Book title cannot be blank")
    private String title;

    @Schema(description="Könyv kiadásának éve", example = "20000")
    @Positive(message = "Publication year cannot be negative")
    private Integer publicationYear;

    @Schema(description="Szerző azonosítója", example = "1")
    @NotNull(message = "Author ID cannot be blank")
    private Long authorId;
}
