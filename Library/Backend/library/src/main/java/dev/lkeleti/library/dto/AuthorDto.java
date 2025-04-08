package dev.lkeleti.library.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorDto {

    @Schema(description="Szerző azonosítója", example = "1")
    private Long id;
    @Schema(description="Szerző neve", example = "John Doe")
    private String name;
    @Schema(description="Szerző születési éve", example = "1975")
    private Integer birthYear;
    @Schema(description="Szerző nemzetisége", example = "Magyar")
    private String nationality;
    @JsonManagedReference
    @Schema(description="Szerző által írt könyvek listája")
    private List<BookDto> books = new ArrayList<>();
}
