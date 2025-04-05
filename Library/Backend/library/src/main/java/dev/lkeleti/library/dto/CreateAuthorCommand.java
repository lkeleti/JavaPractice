package dev.lkeleti.library.dto;

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
    @NotBlank(message = "Author name cannot be blank")
    private String name;

    @NotNull(message = "Birth year must be provided")
    @Positive
    @PastOrPresent
    private Integer birthYear;

    @NotBlank(message = "Nationality cannot be blank")
    private String nationality;
}
