package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.*;
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
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("Security IT")
class SecurityIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository  projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private String baseUrl;

    private Task savedTaskOne;
    private Project savedProject;
    private User savedUser;

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
        baseUrl = "http://localhost:%d".formatted(port);
    }

    // -------------------------------------------------------------------------
    // 401 – Authentikáció hiánya
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/users - credentials nélkül 401-et ad")
    void getUsers_WithoutCredentials_Returns401() {
        restTestClient.get()
                .uri(baseUrl + "/api/users")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getStatus()).isEqualTo(401);
                    assertThat(error.getError()).isEqualTo("Unauthorized");
                });
    }

    @Test
    @DisplayName("POST /api/users - credentials nélkül 401-et ad")
    void createUser_WithoutCredentials_Returns401() {
        restTestClient.post()
                .uri(baseUrl + "/api/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("DELETE /api/users - credentials nélkül 401-et ad")
    void deleteUser_WithoutCredentials_Returns401() {
        restTestClient.delete()
                .uri(baseUrl + "/api/users/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // -------------------------------------------------------------------------
    // 401 – Hibás credentials
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/users - rossz jelszóval 401-et ad")
    void getUsers_WithWrongPassword_Returns401() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "wrongpassword"))
                .build()
                .get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/users - nem létező userrel 401-et ad")
    void getUsers_WithUnknownUser_Returns401() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("ghost", "password"))
                .build()
                .get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // -------------------------------------------------------------------------
    // 403 – Jogosultság hiánya
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/users - USER szerepkörrel 403-at ad")
    void deleteUser_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .delete()
                .uri("/api/users/%d".formatted(savedUser.getId()))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getStatus()).isEqualTo(403);
                    assertThat(error.getError()).isEqualTo("FORBIDDEN");
                });
    }

    @Test
    @DisplayName("POST /api/users - USER szerepkörrel 403-at ad")
    void createUser_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", "New.User@email.com"))
                .exchange()
                .expectStatus().isForbidden();
    }

    // -------------------------------------------------------------------------
    // 200 – Swagger publikusan elérhető
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /swagger-ui/index.html - authentikáció nélkül elérhető")
    void swaggerUi_WithoutCredentials_IsAccessible() {
        restTestClient.get()
                .uri(baseUrl + "/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /v3/api-docs - authentikáció nélkül elérhető")
    void apiDocs_WithoutCredentials_IsAccessible() {
        restTestClient.get()
                .uri(baseUrl + "/v3/api-docs")
                .exchange()
                .expectStatus().isOk();
    }

    // -------------------------------------------------------------------------
    // 200 – ADMIN minden végpontot elér
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/users - ADMIN szerepkörrel sikeres")
    void getUsers_WithAdminRole_Returns200() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("admin", "admin"))
                .build()
                .get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk();
    }

    // -------------------------------------------------------------------------
    // Project végpontok – 401
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/projects - credentials nélkül 401-et ad")
    void getProjects_WithoutCredentials_Returns401() {
        restTestClient.get()
                .uri(baseUrl + "/api/projects")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/projects - credentials nélkül 401-et ad")
    void createProject_WithoutCredentials_Returns401() {
        restTestClient.post()
                .uri(baseUrl + "/api/projects")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("DELETE /api/projects - credentials nélkül 401-et ad")
    void deleteProject_WithoutCredentials_Returns401() {
        restTestClient.delete()
                .uri(baseUrl + "/api/projects/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/projects/{id}/users/{id} - credentials nélkül 401-et ad")
    void assignUserToProject_WithoutCredentials_Returns401() {
        restTestClient.post()
                .uri(baseUrl + "/api/projects/1/users/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // -------------------------------------------------------------------------
    // Project végpontok – 403
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/projects - USER szerepkörrel 403-at ad")
    void createProject_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .post()
                .uri("/api/projects")
                .body(new CreateProjectRequest("New Project", "This is a new project"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("DELETE /api/projects - USER szerepkörrel 403-at ad")
    void deleteProject_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .delete()
                .uri("/api/projects/1")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("POST /api/projects/{id}/users/{id} - USER szerepkörrel 403-at ad")
    void assignUserToProject_WithUserRole_Returns403() {
        User user = userRepository.save(new User(null, "John Doe", "John.Doe@email.com", null, null, null));
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .post()
                .uri("/api/projects/%d/users/%d".formatted(savedProject.getId(), user.getId()))
                .exchange()
                .expectStatus().isForbidden();
    }

    // -------------------------------------------------------------------------
    // Task végpontok – 401
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/tasks - credentials nélkül 401-et ad")
    void getTasks_WithoutCredentials_Returns401() {
        restTestClient.get()
                .uri(baseUrl + "/api/tasks")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/tasks - credentials nélkül 401-et ad")
    void createTask_WithoutCredentials_Returns401() {
        restTestClient.post()
                .uri(baseUrl + "/api/tasks")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - credentials nélkül 401-et ad")
    void deleteTask_WithoutCredentials_Returns401() {
        restTestClient.delete()
                .uri(baseUrl + "/api/tasks/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - credentials nélkül 401-et ad")
    void updateTask_WithoutCredentials_Returns401() {
        restTestClient.put()
                .uri(baseUrl + "/api/tasks/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status - credentials nélkül 401-et ad")
    void changeTaskStatus_WithoutCredentials_Returns401() {
        restTestClient.patch()
                .uri(baseUrl + "/api/tasks/1/status")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/assignee - credentials nélkül 401-et ad")
    void assignUserToTask_WithoutCredentials_Returns401() {
        restTestClient.patch()
                .uri(baseUrl + "/api/tasks/1/assignee")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // -------------------------------------------------------------------------
    // Task végpontok – 403
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/tasks - USER szerepkörrel 403-at ad")
    void createTask_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .post()
                .uri("/api/tasks")
                .body(new CreateTaskRequest("New Task", "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - USER szerepkörrel 403-at ad")
    void deleteTask_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .delete()
                .uri("/api/tasks/1")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - USER szerepkörrel 403-at ad")
    void updateTask_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .put()
                .uri("/api/tasks/1")
                .body(new UpdateTaskRequest("Updated Task", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status - USER szerepkörrel 403-at ad")
    void changeTaskStatus_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .patch()
                .uri("/api/tasks/1/status")
                .body(new UpdateTaskStatusRequest(Status.IN_PROGRESS))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/assignee - USER szerepkörrel 403-at ad")
    void assignUserToTask_WithUserRole_Returns403() {
        restTestClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build()
                .patch()
                .uri("/api/tasks/%d/assignee".formatted(savedUser.getId()))
                .body(new AssigneeTaskRequest(savedUser.getId()))
                .exchange()
                .expectStatus().isForbidden();
    }
}