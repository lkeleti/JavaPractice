package dev.lkeleti.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAuthorCommand {

    @Schema(description="Szerző neve", example = "John Doe")
    @NotBlank
    private String name;

    @Schema(description="Szerző születési éve", example = "1975")
    @NotBlank
    @Positive
    private Integer birthYear;

    @Schema(description="Szerző nemzetisége", example = "Magyar")
    @NotBlank
    private String nationality;
}
