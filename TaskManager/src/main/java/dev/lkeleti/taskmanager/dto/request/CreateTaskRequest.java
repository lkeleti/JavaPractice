package dev.lkeleti.taskmanager.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTaskRequest {
    @NotEmpty(message = "Title is required")
    private String title;
    @NotEmpty(message = "Description is required")
    private String description;
    @Future(message = "Invalid due date")
    private LocalDate dueDate;
    @Nullable
    private Long assigneeId;
    @NotNull(message = "Project ID is required")
    private Long projectId;
}
