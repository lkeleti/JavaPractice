package dev.lkeleti.taskmanager.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssigneeTaskRequest {
    private Long assigneeId;
}
