package dev.lkeleti.library.dto;

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

    @NotBlank
    private String name;
    @NotBlank
    @Positive
    private Integer birthYear;
    @NotBlank
    private String nationality;
}
