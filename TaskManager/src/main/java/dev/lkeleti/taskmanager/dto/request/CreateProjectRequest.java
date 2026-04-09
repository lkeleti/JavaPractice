package dev.lkeleti.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProjectRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
