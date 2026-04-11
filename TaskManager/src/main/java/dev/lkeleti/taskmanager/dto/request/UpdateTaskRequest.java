package dev.lkeleti.taskmanager.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateTaskRequest {
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @Future
    private LocalDate dueDate;
}
