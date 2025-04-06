package dev.lkeleti.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateAuthorCommand {

    @Schema(description="Szerző neve", example = "John Doe")
    @NotBlank(message = "Author name cannot be blank")
    private String name;

    @Schema(description="Szerző születési éve", example = "1975")
    @NotNull(message = "Birth year must be provided")
    @Positive
    @PastOrPresent
    private Integer birthYear;

    @Schema(description="Szerző nemzetisége", example = "Magyar")
    @NotBlank(message = "Nationality cannot be blank")
    private String nationality;
}
