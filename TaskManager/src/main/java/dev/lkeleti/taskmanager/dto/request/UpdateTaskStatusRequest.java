package dev.lkeleti.taskmanager.dto.request;

import dev.lkeleti.taskmanager.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTaskStatusRequest {
    @NotNull
    private Status status;
}
