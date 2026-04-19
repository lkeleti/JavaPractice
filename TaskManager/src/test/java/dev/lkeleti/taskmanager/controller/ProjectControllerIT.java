package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.CreateProjectRequest;
import dev.lkeleti.taskmanager.dto.response.ProjectResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ErrorResponse;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("ProjectController IT")
class ProjectControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project savedProjectOne;
    private Project savedProjectTwo;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        savedProjectOne = projectRepository.save(new Project(null,"Project 01","This is project 1", LocalDateTime.now(),new ArrayList<>()));
        savedProjectTwo = projectRepository.save(new Project(null,"Project 02","This is project 2", LocalDateTime.now(),new ArrayList<>()));
    }

    @Test
    @DisplayName("Get all projects successfully")
    void getAllProjects_Success() {
        restTestClient.get()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ProjectResponse>>() {
                })
                .value(projects -> {
                    assertThat(projects).isNotNull();
                    assertThat(projects).hasSize(2);
                    assertThat(projects)
                            .extracting(ProjectResponse::getName)
                            .containsExactlyInAnyOrder("Project 01", "Project 02");
                });
    }

    @Test
    @DisplayName("Get all projects successfully, but the list is empty")
    void getAllProjects_Empty() {
        projectRepository.deleteAll();

        restTestClient.get()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ProjectResponse>>() {})
                .value(projects -> {
                    assertThat(projects).isNotNull();
                    assertThat(projects).isEmpty();
                });
    }

    @Test
    @DisplayName("Get project by id successfully")
    void getProjectById_Success() {
        restTestClient.get()
                .uri("http://localhost:%d/api/projects/%d".formatted(port, savedProjectOne.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ProjectResponse>() {
                })
                .value(project -> {
                    assertThat(project).isNotNull();
                    assertThat(project.getName()).isEqualTo("Project 01");
                });
    }

    @Test
    @DisplayName("Get project by id returns not found error")
    void getProjectById_Error() {
        restTestClient.get()
                .uri("http://localhost:%d/api/projects/%d".formatted(port, 999999))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find project with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Create project successfully")
    void createProject_Success() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .body(new CreateProjectRequest("New Project", "This is a new project"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ProjectResponse>() {
                })
                .value(project -> {
                    assertThat(project).isNotNull();
                    assertThat(project.getName()).isEqualTo("New Project");
                    assertThat(project.getDescription()).isEqualTo("This is a new project");
                    assertThat(project.getCreatedAt()).isNotNull();
                    assertThat(project.getUserIds()).isEmpty();
                });
        assertThat(projectRepository.findAll())
                .extracting(Project::getName)
                .contains("New Project");
    }

    @Test
    @DisplayName("Create project with null name returns validation error")
    void createProjectNameNull_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .body(new CreateProjectRequest(null, "New project"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("name: Name is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create project with empty name returns validation error")
    void createProjectNameEmpty_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .body(new CreateProjectRequest("", "New project"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("name: Name is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create project with null description returns validation error")
    void createProjectDescriptionNull_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .body(new CreateProjectRequest("New project", null))
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
    @DisplayName("Create project with empty description returns validation error")
    void createProjectDescriptionEmpty_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects".formatted(port))
                .body(new CreateProjectRequest("New project", ""))
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
    @DisplayName("Delete project by id successfully")
    void deleteProjectById_Success() {
        restTestClient.delete()
                .uri("http://localhost:%d/api/projects/%d".formatted(port, savedProjectOne.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        assertThat(projectRepository.findAll())
                .extracting(Project::getId)
                .doesNotContain(savedProjectOne.getId());
        assertThat(projectRepository.existsById(savedProjectOne.getId())).isFalse();
    }

    @Test
    @DisplayName("Delete project by id returns not found error")
    void deleteProjectById_Error() {
        restTestClient.delete()
                .uri("http://localhost:%d/api/projects/%d".formatted(port, 999999))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find project with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Assign user to project successfully")
    void assignUserToProject_Success() {
        User user = userRepository.save(new User(null, "John Doe", "John.Doe@email.com",null,null,null));
        restTestClient.post()
                .uri("http://localhost:%d/api/projects/%d/users/%d".formatted(port, savedProjectOne.getId(), user.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ProjectResponse>() {
                })
                .value(project -> {
                    assertThat(project).isNotNull();
                    assertThat(project.getName()).isEqualTo(savedProjectOne.getName());
                    assertThat(project.getDescription()).isEqualTo(savedProjectOne.getDescription());
                    assertThat(project.getCreatedAt()).isNotNull();
                    assertThat(project.getUserIds()).contains(user.getId());
                });
    }

    @Test
    @DisplayName("Assign non exits user to project error")
    void assignNonExistsUserToProject_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/projects/%d/users/%d".formatted(port, savedProjectOne.getId(), 999999))
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
    @DisplayName("Assign user to non exits project error")
    void assignUserToNonExistsProject_Error() {
        User user = userRepository.save(new User(null, "John Doe", "John.Doe@email.com",null,new ArrayList<>(),new ArrayList<>()));
        restTestClient.post()
                .uri("http://localhost:%d/api/projects/%d/users/%d".formatted(port, 999999, user.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find project with id: 999999");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Assign user already assign to project error")
    void assignUserAlreadyAssignToProject_Error() {
        User user = userRepository.save(
                new User(null, "John Doe", "John.Doe@email.com", null, new ArrayList<>(), new ArrayList<>())
        );

        Project project = new Project(
                null,
                savedProjectOne.getName(),
                savedProjectOne.getDescription(),
                savedProjectOne.getCreatedAt(),
                new ArrayList<>()
        );

        project.getUsers().add(user);
        user.getProjects().add(project);

        project = projectRepository.save(project);
        userRepository.save(user);

        restTestClient.post()
                .uri("http://localhost:%d/api/projects/%d/users/%d".formatted(port, project.getId(), user.getId()))
                .exchange()
                .expectStatus().isEqualTo(CONFLICT)
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("A felhasználó már hozzá van rendelve a projekthez.");
                    assertThat(error.getStatus()).isEqualTo(409);
                    assertThat(error.getError()).isEqualTo("CONFLICT");
                });
    }
}
