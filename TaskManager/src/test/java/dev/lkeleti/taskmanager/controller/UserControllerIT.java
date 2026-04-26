package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.CreateUserRequest;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("UserController IT")
class UserControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User savedUserOne;

    private RestTestClient userClient;
    private RestTestClient adminClient;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAllInBatch();
        userRepository.deleteAll();
        projectRepository.deleteAll();

        savedUserOne = userRepository.save(new User(null,"John Doe","John.Doe@email.com", LocalDateTime.now(),null,null));
        userRepository.save(new User(null,"Joe Doe","Joe.Doe@email.com", LocalDateTime.now(),null,null));

        String baseUrl = "http://localhost:%d".formatted(port);

        userClient = restTestClient
                .mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("user", "password"))
                .build();

        adminClient = restTestClient
                .mutate()
                .baseUrl(baseUrl)
                .defaultHeaders(h -> h.setBasicAuth("admin", "admin"))
                .build();
    }

    @Test
    @DisplayName("Get all users successfully")
    void getAllUsers_Success() {
        userClient.get()
                .uri("/api/users?page=0&size=10&sortBy=id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[*].name")
                .value(names -> {
                    assertThat((Iterable<String>) names)
                            .containsExactlyInAnyOrder("John Doe", "Joe Doe");
                });
    }

    @Test
    @DisplayName("Get all users successfully, but the list is empty")
    void getAllUser_Empty() {
        userRepository.deleteAll();
        userClient.get()
                .uri("/api/users?page=0&size=10&sortBy=id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Get user by id successfully")
    void getUserById_Success() {
        userClient.get()
                .uri("/api/users/id/%d".formatted(savedUserOne.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<UserResponse>() {
                })
                .value(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getName()).isEqualTo("John Doe");
                });
    }

    @Test
    @DisplayName("Get user by id returns not found error")
    void getUserById_Error() {
        userClient.get()
                .uri("/api/users/id/999999")
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
    @DisplayName("Get user by email successfully")
    void getUserByEmail_Success() {
        userClient.get()
                .uri("/api/users/email/%s".formatted(savedUserOne.getEmail()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<UserResponse>() {
                })
                .value(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getName()).isEqualTo("John Doe");
                });
    }

    @Test
    @DisplayName("Get user by email returns not found error")
    void getUserByEmail_Error() {
        userClient.get()
                .uri("/api/users/email/nonexist@email.com")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Cannot find user with email: nonexist@email.com");
                    assertThat(error.getStatus()).isEqualTo(404);
                    assertThat(error.getError()).isEqualTo("NOT_FOUND");
                });
    }

    @Test
    @DisplayName("Delete user by id successfully")
    void deleteUserById_Success() {
        adminClient.delete()
                .uri("/api/users/%d".formatted(savedUserOne.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        assertThat(userRepository.findAll())
                .extracting(User::getId)
                .doesNotContain(savedUserOne.getId());
    }

    @Test
    @DisplayName("Delete user by id returns not found error")
    void deleteUserById_Error() {
        adminClient.delete()
                .uri("/api/users/999999")
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
    @DisplayName("Create user successfully")
    void createUser_Success() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", "New.User@email.com"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<UserResponse>() {
                })
                .value(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getName()).isEqualTo("New User");
                    assertThat(user.getEmail()).isEqualTo("New.User@email.com");
                    assertThat(user.getCreatedAt()).isNotNull();
                    assertThat(user.getProjectIds()).isEmpty();
                    assertThat(user.getTaskIds()).isEmpty();
                });
        assertThat(userRepository.findAll())
                .extracting(User::getEmail)
                .contains("New.User@email.com");
    }

    @Test
    @DisplayName("Create user with null name returns validation error")
    void createUserNameNull_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest(null, "New.User@email.com"))
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
    @DisplayName("Create user with empty name returns validation error")
    void createUserNameEmpty_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("", "New.User@email.com"))
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
    @DisplayName("Create user with whitespace name returns validation error")
    void createUserNameWhitespace_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("   ", "New.User@email.com"))
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
    @DisplayName("Create user with null email returns validation error")
    void createUserEmailNull_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", null))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("email: Email is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create user with empty email returns validation error")
    void createUserEmailEmpty_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", ""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("email: Email is required");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create user with whitespace email returns validation error")
    void createUserEmailWhitespace_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", "     "))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).contains("email:");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isNotEmpty();
                });
    }

    @Test
    @DisplayName("Create user with invalid email format returns validation error")
    void createUserEmailBadFormat_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("New User", "New.Useremail.com"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("email: Invalid email format");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("VALIDATION_ERROR");
                });
    }

    @Test
    @DisplayName("Create user with existing email returns bad request error")
    void createUser_EmailAlreadyExists_Error() {
        adminClient.post()
                .uri("/api/users")
                .body(new CreateUserRequest("John Doe", "John.Doe@email.com"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                    assertThat(error.getMessage()).isEqualTo("Email already exists");
                    assertThat(error.getStatus()).isEqualTo(400);
                    assertThat(error.getError()).isEqualTo("BAD_REQUEST");
                });
    }


}

