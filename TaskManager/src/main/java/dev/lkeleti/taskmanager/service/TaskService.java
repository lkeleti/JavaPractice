package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.AssigneeTaskRequest;
import dev.lkeleti.taskmanager.dto.request.CreateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskStatusRequest;
import dev.lkeleti.taskmanager.dto.response.TaskResponse;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(int page, int size, String sortBy) {
        log.info("Fetching tasks page={}, size={}, sortBy={}", page, size, sortBy);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        Page<TaskResponse> result = taskRepository.findAll(pageable)
                .map(this::mapToResponse);

        log.debug("Fetched {} tasks (total elements: {})",
                result.getNumberOfElements(),
                result.getTotalElements());

        return result;
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        log.info("Fetching task by id={}", id);

        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.warn("Task not found with id={}", id);
            return new EntityNotFoundException("Cannot find task with id: " + id);
        });

        log.debug("Task found: id={}, title={}", task.getId(), task.getTitle());
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest command) {
        log.debug("Creating task: {}", command);

        Project project = projectRepository.findById(command.getProjectId()).orElseThrow(() -> {
            log.warn("Project not found with id={}", command.getProjectId());
            return new EntityNotFoundException("Cannot find project with id: " + command.getProjectId());
        });

        Task task = new Task();
        task.setProject(project);

        User user = null;

        if (command.getAssigneeId() != null) {
            user = userRepository.findById(command.getAssigneeId()).orElseThrow(() -> {
                log.warn("User not found with id={}", command.getAssigneeId());
                return new EntityNotFoundException("Cannot find user with id: " + command.getAssigneeId());
            });

            if (!project.getUsers().contains(user)) {
                log.warn("User {} not part of project {}", command.getAssigneeId(), project.getId());
                throw new ValidationErrorException("userId=" + command.getAssigneeId() + " is not part of projectId=" + project.getId());
            }
        }

        if (command.getTitle() == null || command.getTitle().isBlank()) {
            log.warn("Task creation failed - invalid title");
            throw new ValidationErrorException("title: Title is required");
        }

        if (command.getDescription() == null || command.getDescription().isBlank()) {
            log.warn("Task creation failed - invalid description");
            throw new ValidationErrorException("description: Description is required");
        }

        if (command.getDueDate() == null || command.getDueDate().isBefore(LocalDate.now())) {
            log.warn("Task creation failed - invalid due date: {}", command.getDueDate());
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

        Task saved = taskRepository.save(task);
        log.info("Task created with id={}", saved.getId());

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with id={}", id);

        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.warn("Task not found for deletion with id={}", id);
            return new EntityNotFoundException("Cannot find task with id: " + id);
        });

        taskRepository.delete(task);
        log.info("Task deleted with id={}", id);
    }

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest command) {
        log.debug("Updating task id={} with {}", id, command);

        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.warn("Task not found for update with id={}", id);
            return new EntityNotFoundException("Cannot find task with id: " + id);
        });

        if (command.getTitle() != null && !command.getTitle().isBlank()) {
            task.setTitle(command.getTitle());
        }

        if (command.getDescription() != null && !command.getDescription().isBlank()) {
            task.setDescription(command.getDescription());
        }

        if (command.getDueDate() != null && !command.getDueDate().isAfter(LocalDate.now())) {
            task.setDueDate(command.getDueDate());
        }

        Task updated = taskRepository.save(task);
        log.info("Task updated with id={}", updated.getId());

        return mapToResponse(updated);
    }

    @Transactional
    public TaskResponse changeStatus(Long id, @Valid UpdateTaskStatusRequest command) {
        log.debug("Changing status of task id={} to {}", id, command.getStatus());

        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.warn("Task not found for status change with id={}", id);
            return new EntityNotFoundException("Cannot find task with id: " + id);
        });

        if (command.getStatus() == null) {
            log.warn("Status change failed - null status for task id={}", id);
            throw new ValidationErrorException("Cannot change status of task to null");
        }

        if (Math.abs(task.getStatus().getOrder() - command.getStatus().getOrder()) != 1) {
            log.warn("Invalid status transition from {} to {} for task id={}",
                    task.getStatus(), command.getStatus(), id);
            throw new ValidationErrorException("Cannot change status of task to status: " + command.getStatus());
        }

        task.setStatus(command.getStatus());
        Task updated = taskRepository.save(task);

        log.info("Task status updated: id={}, newStatus={}", id, command.getStatus());
        return mapToResponse(updated);
    }

    @Transactional
    public TaskResponse assigneeTask(Long id, @Valid AssigneeTaskRequest command) {
        log.debug("Assigning user {} to task {}", command.getAssigneeId(), id);

        Task task = taskRepository.findById(id).orElseThrow(() -> {
            log.warn("Task not found for assignment with id={}", id);
            return new EntityNotFoundException("Cannot find task with id: " + id);
        });

        if (command.getAssigneeId() == null) {
            log.warn("Assignment failed - assigneeId is null for task id={}", id);
            throw new ValidationErrorException("assigneeId cannot be null");
        }

        User user = userRepository.findById(command.getAssigneeId()).orElseThrow(() -> {
            log.warn("User not found with id={}", command.getAssigneeId());
            return new EntityNotFoundException("Cannot find user with id: " + command.getAssigneeId());
        });

        Project project = task.getProject();
        if (!project.getUsers().contains(user)) {
            log.warn("User {} not part of project {} for task {}",
                    command.getAssigneeId(), project.getId(), id);
            throw new ValidationErrorException(
                    "Cannot assign task to user, because user not part of the project. user: " + command.getAssigneeId()
            );
        }

        if (task.getAssignee() != null) {
            task.getAssignee().getTasks().remove(task);
        }

        task.setAssignee(user);

        if (task.getStatus() == null) {
            task.setStatus(Status.getFirst());
        }

        user.getTasks().add(task);

        Task updated = taskRepository.save(task);
        log.info("User {} assigned to task {}", command.getAssigneeId(), id);

        return mapToResponse(updated);
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
