package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.AssigneeTaskRequest;
import dev.lkeleti.taskmanager.dto.request.CreateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskStatusRequest;
import dev.lkeleti.taskmanager.dto.response.TaskResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Status;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ErrorResponse;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
import dev.lkeleti.taskmanager.repository.TaskRepository;
import dev.lkeleti.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("TaskController IT")
class TaskControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Task savedTaskOne;
    private Project savedProject;
    private User savedUser;

    private RestTestClient userClient;
    private RestTestClient adminClient;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAllInBatch();
        userRepository.deleteAll();
        projectRepository.deleteAll();

        savedProject = new Project(null, "Project 01", "This is project 1", LocalDateTime.now(), new ArrayList<>());
        savedUser = new User(null, "User01", "User01@email.com", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        savedUser = userRepository.save(savedUser);

        savedProject.getUsers().add(savedUser);
        savedUser.getProjects().add(savedProject);

        Task taskOne = new Task(null, "Task 01", "This is task 1", Status.TODO, LocalDate.now().plusDays(5), LocalDateTime.now(), null, null);
        Task taskTwo = new Task(null, "Task 02", "This is task 2", Status.TODO, LocalDate.now().plusDays(5), LocalDateTime.now(), null, null);

        savedUser.getTasks().add(taskOne);
        savedUser.getTasks().add(taskTwo);
        taskOne.setAssignee(savedUser);
        taskTwo.setAssignee(savedUser);
        taskOne.setProject(savedProject);
        taskTwo.setProject(savedProject);

        projectRepository.save(savedProject);
        userRepository.save(savedUser);

        savedTaskOne = taskRepository.save(taskOne);
        taskRepository.save(taskTwo);

        String baseUrl = "http://localhost:%d".formatted(port);

        userClient = restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build();

        adminClient = restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("admin", "admin"))
                .build();
    }

    @Test
    @DisplayName("Get all tasks successfully")
    void getAllTasks_Success() {
        userClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[*].title")
                .value(tasks -> {
                    assertThat((Iterable<String>) tasks)
                            .containsExactlyInAnyOrder("Task 01", "Task 02");
                });
    }

    @Test
    @DisplayName("Get all tasks successfully, but the list is empty")
    void getAllTasks_Empty() {
        taskRepository.deleteAllInBatch();

        userClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Get task by id successfully")
    void getTaskById_Success() {
        userClient.get()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Task 01");
                });
    }

    @Test
    @DisplayName("Get task by id returns not found error")
    void getTaskById_Error() {
        userClient.get()
                .uri("/api/tasks/999999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find task with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Create task without user successfully")
    void createTaskWithoutUser_Success() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("New Task", "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("New Task");
                    assertThat(task.getDescription()).isEqualTo("This is a new task");
                    assertThat(task.getCreatedAt()).isNotNull();
                    assertThat(task.getAssigneeId()).isNull();
                    assertThat(task.getProjectId()).isEqualTo(savedProject.getId());
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("New Task");
    }

    @Test
    @DisplayName("Create task with user successfully")
    void createTaskWithUser_Success() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("New Task", "This is a new task", LocalDate.now().plusDays(100), savedUser.getId(), savedProject.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("New Task");
                    assertThat(task.getDescription()).isEqualTo("This is a new task");
                    assertThat(task.getCreatedAt()).isNotNull();
                    assertThat(task.getAssigneeId()).isEqualTo(savedUser.getId());
                    assertThat(task.getProjectId()).isEqualTo(savedProject.getId());
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("New Task");
    }

    @Test
    @DisplayName("Create task with null title returns validation error")
    void createTaskTitleNull_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest(null, "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("title: Title is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with empty title returns validation error")
    void createTaskTitleEmpty_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("", "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("title: Title is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with whitespace title returns validation error")
    void createTaskTitleWhitespace_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("   ", "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("title: Title is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with null description returns validation error")
    void createTaskDecriptionNull_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", null, LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("description: Description is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with empty description returns validation error")
    void createTaskDecriptionEmpty_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("description: Description is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with whitespace description returns validation error")
    void createTaskDecriptionWhitespace_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "   ", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("description: Description is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with non exists user returns error")
    void createTaskNonExists_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "This is task 1", LocalDate.now().plusDays(100), 999999L, savedProject.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find user with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Create task with user not part of project returns error")
    void createTaskUserNotPartOfProject_Error() {
        User user = userRepository.save(new User(null, "New User", "New.User@email.com", LocalDateTime.now(), null, null));

        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "This is task 1", LocalDate.now().plusDays(100), user.getId(), savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("userId=" + user.getId() + " is not part of projectId=" + savedProject.getId());
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with null due date returns validation error")
    void createTaskDueDateNull_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "This is task 1", null, null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("dueDate: Due date is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create task with past due date returns validation error")
    void createTaskDueDateBefore_Error() {
        adminClient.post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("Task 01", "This is task 1", LocalDate.now().minusYears(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("dueDate: Invalid due date");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Delete task by id successfully")
    void deleteTaskById_Success() {
        adminClient.delete()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(taskRepository.findAll())
                .extracting(Task::getId)
                .doesNotContain(savedTaskOne.getId());
        assertThat(taskRepository.existsById(savedTaskOne.getId())).isFalse();
    }

    @Test
    @DisplayName("Delete non exists task by id returns not found error")
    void deleteNonExistsTaskById_Error() {
        adminClient.delete()
                .uri("/api/tasks/999999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find task with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Update task successfully")
    void updateTask_Success() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Updated Task");
                    assertThat(task.getDescription()).isEqualTo("This is an updated task");
                    assertThat(task.getCreatedAt()).isNotNull();
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("Updated Task");
    }

    @Test
    @DisplayName("Update non exists task error")
    void updateNonExistsTask_Error() {
        adminClient.put()
                .uri("/api/tasks/999999")
                .body(new UpdateTaskRequest("Updated Task", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find task with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Update task with null title returns validation error")
    void updateTaskNullTitle_Error() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest(null, "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("title: Title is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Update task with empty title returns validation error")
    void updateTaskEmptyTitle_Error() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("title: Title is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Update task with whitespace title keeps original title")
    void updateTaskWhitespaceTitle_Success() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("    ", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Task 01");
                    assertThat(task.getDescription()).isEqualTo("This is an updated task");
                    assertThat(task.getCreatedAt()).isNotNull();
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("Task 01");
    }

    @Test
    @DisplayName("Update task with null description returns validation error")
    void updateTaskNullDescription_Error() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", null, LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("description: Description is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Update task with empty description returns validation error")
    void updateTaskEmptyDescription_Error() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("description: Description is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Update task with whitespace description keeps original description")
    void updateTaskWhitespaceDescription_Success() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "    ", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Updated Task");
                    assertThat(task.getDescription()).isEqualTo("This is task 1");
                    assertThat(task.getCreatedAt()).isNotNull();
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getDescription)
                .contains("This is task 1");
    }

    @Test
    @DisplayName("Update task with null due date keeps original due date")
    void updateTaskDueDateNull_Success() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "    ", null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Updated Task");
                    assertThat(task.getDescription()).isEqualTo("This is task 1");
                    assertThat(task.getDueDate()).isEqualTo(savedTaskOne.getDueDate());
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getDueDate)
                .contains(savedTaskOne.getDueDate());
    }

    @Test
    @DisplayName("Update task with past due date returns validation error")
    void updateTaskDueDateBefore_Error() {
        adminClient.put()
                .uri("/api/tasks/%d".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "    ", LocalDate.now().minusYears(1)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("dueDate: Invalid due date");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Change task status successfully")
    void changeTaskStatus_Success() {
        adminClient.patch()
                .uri("/api/tasks/%d/status".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskStatusRequest(Status.IN_PROGRESS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Task 01");
                    assertThat(task.getStatus()).isEqualTo(Status.IN_PROGRESS);
                });

        assertThat(taskRepository.findAll())
                .extracting(Task::getStatus)
                .contains(Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Change non exists task status error")
    void changeNonExistsTaskStatus_Error() {
        adminClient.patch()
                .uri("/api/tasks/999999/status")
                .body(new UpdateTaskStatusRequest(Status.IN_PROGRESS))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find task with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Change task status to null error")
    void changeTaskStatusToNull_Error() {
        adminClient.patch()
                .uri("/api/tasks/%d/status".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskStatusRequest(null))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("status: Cannot change status of task to null");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Change task status to invalid status error")
    void changeTaskStatusToInvalid_Error() {
        adminClient.patch()
                .uri("/api/tasks/%d/status".formatted(savedTaskOne.getId()))
                .body(new UpdateTaskStatusRequest(Status.DONE))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot change status of task to status: " + Status.DONE);
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Assign user to task successfully")
    void assignUserToTask_Success() {
        Task taskTmp = taskRepository.findById(savedTaskOne.getId()).get();
        taskTmp.setAssignee(null);
        taskRepository.save(taskTmp);

        adminClient.patch()
                .uri("/api/tasks/%d/assignee".formatted(savedTaskOne.getId()))
                .body(new AssigneeTaskRequest(savedUser.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {})
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Task 01");
                    assertThat(task.getAssigneeId()).isEqualTo(savedUser.getId());
                });

        assertThat(taskRepository.findAll())
                .extracting(task -> task.getAssignee().getId())
                .contains(savedUser.getId());
    }

    @Test
    @DisplayName("Assign user to non exists task error")
    void assignUserToNonExistsTask_Error() {
        adminClient.patch()
                .uri("/api/tasks/999999/assignee")
                .body(new AssigneeTaskRequest(savedUser.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find task with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Assign non exists user to task error")
    void assignNonExistsUserToTask_Error() {
        adminClient.patch()
                .uri("/api/tasks/%d/assignee".formatted(savedTaskOne.getId()))
                .body(new AssigneeTaskRequest(999999L))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find user with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Assign null user to task error")
    void assignNullUserToTask_Error() {
        adminClient.patch()
                .uri("/api/tasks/%d/assignee".formatted(savedTaskOne.getId()))
                .body(new AssigneeTaskRequest(null))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("assigneeId cannot be null");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Assign user to task when user is not part of project returns error")
    void assignUserToTaskNoProject_Error() {
        Task taskTmp = taskRepository.findById(savedTaskOne.getId()).get();
        taskTmp.setAssignee(null);
        taskRepository.save(taskTmp);
        savedUser.setProjects(null);
        userRepository.save(savedUser);

        adminClient.patch()
                .uri("/api/tasks/%d/assignee".formatted(savedTaskOne.getId()))
                .body(new AssigneeTaskRequest(savedUser.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot assign task to user, because user not part of the project. user: " + savedUser.getId());
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }
}