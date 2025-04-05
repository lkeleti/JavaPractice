package dev.lkeleti.library.dto;

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
public class CreateBookCommand {
    @NotBlank(message = "Book ISBN cannot be blank")
    private String isbn;

    @NotBlank(message = "Book title cannot be blank")
    private String title;

    @Positive(message = "Publication year cannot be negative")
    private Integer publicationYear;

    @NotNull(message = "Author ID cannot be blank")
    private Long authorId;
}
