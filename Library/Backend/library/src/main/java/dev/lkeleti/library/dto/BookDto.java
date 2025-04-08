package dev.lkeleti.library.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookDto {

    @Schema(description="Könyv azonosítója", example = "1")
    private Long id;
    @Schema(description="Könyv ISBN száma", example = "1111111111111111111")
    private String isbn;
    @Schema(description="Könyv címe", example = "1")
    private String title;
    @Schema(description="Könyv kiadásának éve", example = "2000")
    private Integer publicationYear;
    @JsonBackReference
    @Schema(description="Könyv szerzője", example = "John Doe")
    private AuthorDto author;
}
