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
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @Future
    private LocalDate dueDate;
    @Nullable
    private Long assigneeId;
    @NotNull
    private Long projectId;
}
