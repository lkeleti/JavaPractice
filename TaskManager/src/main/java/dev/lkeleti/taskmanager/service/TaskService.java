package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.AssigneeTaskRequest;
import dev.lkeleti.taskmanager.dto.request.CreateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskStatusRequest;
import dev.lkeleti.taskmanager.dto.response.TaskResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Status;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ValidationErrorException;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
import dev.lkeleti.taskmanager.repository.TaskRepository;
import dev.lkeleti.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find task with id: " + id)
        );

        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest command) {
        Project project = projectRepository.findById(command.getProjectId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find project with id: " + command.getProjectId())
        );

        Task task = new Task();
        task.setProject(project);
        User user = null;

        if (command.getAssigneeId() != null) {
            user = userRepository.findById(command.getAssigneeId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find user with id: " + command.getAssigneeId())
            );
            if (!project.getUsers().contains(user)) {
                throw new ValidationErrorException("userId=" + command.getAssigneeId() + " is not part of projectId=" + project.getId());
            }
        }

        if (command.getTitle() == null ||  command.getTitle().isEmpty() || command.getTitle().isBlank()) {
            throw new ValidationErrorException("title: Title is required");
        }

        if (command.getDescription() == null ||  command.getDescription().isEmpty() || command.getDescription().isBlank()) {
            throw new ValidationErrorException("description: Description is required");
        }

        if (command.getDueDate() == null || command.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidationErrorException("dueDate: Due date is required");
        }

        task.setAssignee(user);
        task.setStatus(Status.getFirst());
        task.setTitle(command.getTitle());
        task.setDescription(command.getDescription());
        task.setDueDate(command.getDueDate());

        if (user != null) {
            user.getTasks().add(task);
        }
        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find task with id: " + id)
        );
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest command) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find task with id: " + id)
        );

        if (command.getTitle() != null && !command.getTitle().isBlank()) {
            task.setTitle(command.getTitle());
        }
        if (command.getDescription() != null && !command.getDescription().isBlank()) {
            task.setDescription(command.getDescription());
        }

        if (command.getDueDate() != null && !command.getDueDate().isAfter(LocalDate.now())) {
            task.setDueDate(command.getDueDate());
        }

        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse changeStatus(Long id, @Valid UpdateTaskStatusRequest command) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find task with id: " + id)
        );

        if (command.getStatus() == null) {
            throw new ValidationErrorException("Cannot change status of task to null");
        }

        if (Math.abs(task.getStatus().getOrder() - command.getStatus().getOrder()) != 1) {
            throw new ValidationErrorException("Cannot change status of task to status: " + command.getStatus());
        }

        task.setStatus(command.getStatus());
        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse assigneeTask(Long id, @Valid AssigneeTaskRequest command) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find task with id: " + id)
        );

         if (command.getAssigneeId() == null) {
            throw new ValidationErrorException("assigneeId cannot be null");
        }

        User user = userRepository.findById(command.getAssigneeId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with id: " + command.getAssigneeId())
        );

        Project project = task.getProject();
        if (!project.getUsers().contains(user)) {
            throw new ValidationErrorException("Cannot assign task to user, because user not part of the project. user: " + command.getAssigneeId());
        }

        if (task.getAssignee() != null) {
            task.getAssignee().getTasks().remove(task);
        }
        task.setAssignee(user);
        if (task.getStatus() == null) {
            task.setStatus(Status.getFirst());
        }
        user.getTasks().add(task);
        return mapToResponse(taskRepository.save(task));
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = modelMapper.map(task, TaskResponse.class);

        response.setProjectId(task.getProject().getId());

        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
        }

        return response;
    }
}
