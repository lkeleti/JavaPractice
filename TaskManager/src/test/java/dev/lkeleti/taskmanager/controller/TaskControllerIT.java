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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Task savedTaskTwo;
    private Project savedProject;
    private User savedUser;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAllInBatch();
        userRepository.deleteAll();
        projectRepository.deleteAll();

        savedProject = new Project(null,"Project 01", "This is project 1",LocalDateTime.now(),new ArrayList<>());
        savedUser = new User(null,"User01","User01@email.com",LocalDateTime.now(),new ArrayList<>(), new ArrayList<>());
        savedUser = userRepository.save(savedUser);

        savedProject.getUsers().add(savedUser);
        savedUser.getProjects().add(savedProject);
        Task taskOne = new Task(null, "Task 01", "This is task 1", Status.TODO, LocalDate.now().plusDays(5), LocalDateTime.now(), null,null);
        Task taskTwo = new Task(null, "Task 02", "This is task 2", Status.TODO, LocalDate.now().plusDays(5), LocalDateTime.now(), null,null);
        savedUser.getTasks().add(taskOne);
        savedUser.getTasks().add(taskTwo);
        taskOne.setAssignee(savedUser);
        taskTwo.setAssignee(savedUser);
        taskOne.setProject(savedProject);
        taskTwo.setProject(savedProject);

        projectRepository.save(savedProject);
        userRepository.save(savedUser);

        savedTaskOne = taskRepository.save(taskOne);
        savedTaskTwo = taskRepository.save(taskTwo);
    }

    @Test
    @DisplayName("Get all tasks successfully")
    void getAllTasks_Success() {
        restTestClient.get()
                .uri("http://localhost:%d/api/tasks".formatted(port))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<TaskResponse>>() {
                })
                .value(tasks -> {
                    assertThat(tasks).isNotNull();
                    assertThat(tasks).hasSize(2);
                    assertThat(tasks)
                            .extracting(TaskResponse::getTitle)
                            .containsExactlyInAnyOrder("Task 01", "Task 02");
                });
    }

    @Test
    @DisplayName("Get all tasks successfully, but the list is empty")
    void getAllTasks_Empty() {
        taskRepository.deleteAllInBatch();

        restTestClient.get()
                .uri("http://localhost:%d/api/tasks".formatted(port))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<TaskResponse>>() {})
                .value(tasks -> {
                    assertThat(tasks).isNotNull();
                    assertThat(tasks).isEmpty();
                });
    }

    @Test
    @DisplayName("Get task by id successfully")
    void getTaskById_Success() {
        restTestClient.get()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("Task 01");
                });
    }

    @Test
    @DisplayName("Get task by id returns not found error")
    void getTaskById_Error() {
        restTestClient.get()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, 999999))
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
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
                .body(new CreateTaskRequest("New Task", "This is a new task", LocalDate.now().plusDays(100), null, savedProject.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("New Task");
                    assertThat(task.getDescription()).isEqualTo("This is a new task");
                    assertThat(task.getCreatedAt()).isNotNull();
                    assertThat(task.getAssigneeId()).isNull();
                    assertThat(task.getProjectId()).isEqualTo (savedProject.getId());
                });
        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("New Task");;
    }

    @Test
    @DisplayName("Create task with user successfully")
    void createTaskWithUser_Success() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
                .body(new CreateTaskRequest("New Task", "This is a new task", LocalDate.now().plusDays(100), savedUser.getId(), savedProject.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
                .value(task -> {
                    assertThat(task).isNotNull();
                    assertThat(task.getTitle()).isEqualTo("New Task");
                    assertThat(task.getDescription()).isEqualTo("This is a new task");
                    assertThat(task.getCreatedAt()).isNotNull();
                    assertThat(task.getAssigneeId()).isEqualTo(savedUser.getId());
                    assertThat(task.getProjectId()).isEqualTo (savedProject.getId());
                });
        assertThat(taskRepository.findAll())
                .extracting(Task::getTitle)
                .contains("New Task");
    }

    @Test
    @DisplayName("Create task with null title returns validation error")
    void createTaskTitleNull_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
    @DisplayName("Create task with null decription returns validation error")
    void createTaskDecriptionNull_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
    @DisplayName("Create task with empty decription returns validation error")
    void createTaskDecriptionEmpty_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
    @DisplayName("Create task with whitespace decription returns validation error")
    void createTaskDecriptionWhitespace_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        User user = userRepository.save(new User(null, "New User", "New.User@email.com",LocalDateTime.now(), null, null));
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
    @DisplayName("Create task with before due date returns validation error")
    void createTaskDueDateBefore_Error() {
        restTestClient.post()
                .uri("http://localhost:%d/api/tasks".formatted(port))
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
        restTestClient.delete()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        assertThat(taskRepository.findAll())
                .extracting(Task::getId)
                .doesNotContain(savedTaskOne.getId());
        assertThat(taskRepository.existsById(savedTaskOne.getId())).isFalse();
    }

    @Test
    @DisplayName("Delete project by id returns not found error")
    void deleteNonExistsTaskById_Error() {
        restTestClient.delete()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, 999999))
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, 999999L))
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .body(new UpdateTaskRequest("    ", "This is an updated task", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
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
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
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
    @DisplayName("Update task with whitespace description success")
    void updateTaskWhitespaceDescription_Success() {
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "    ", LocalDate.now().plusYears(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
    @DisplayName("Update task with null due date success")
    void updateTaskDueDateNull_Success() {
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
                .body(new UpdateTaskRequest("Updated Task", "    ", null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
    @DisplayName("Update task with before due date returns validation error")
    void updateTaskDueDateBefore_Error() {
        restTestClient.put()
                .uri("http://localhost:%d/api/tasks/%d".formatted(port, savedTaskOne.getId()))
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
    @DisplayName("Change task status success")
    void changeTaskStatus_Success() {
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/status".formatted(port, savedTaskOne.getId()))
                .body(new UpdateTaskStatusRequest(Status.IN_PROGRESS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/status".formatted(port, 999999L))
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
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/status".formatted(port, savedTaskOne.getId()))
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
    @DisplayName("Change task status to invalid error")
    void changeTaskStatusToInvalid_Error() {
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/status".formatted(port, savedTaskOne.getId()))
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
    @DisplayName("Assign user to task success")
    void assignUserToTask_Success() {
        Task taskTmp = taskRepository.findById(savedTaskOne.getId()).get();
        taskTmp.setAssignee(null);
        taskRepository.save(taskTmp);

        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/assignee".formatted(port, savedTaskOne.getId()))
                .body(new AssigneeTaskRequest(savedUser.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TaskResponse>() {
                })
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
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/assignee".formatted(port, 999999L))
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
    @DisplayName("Assign non exist user to task error")
    void assignNonExistsUserToTask_Error() {
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/assignee".formatted(port, savedTaskOne.getId()))
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
        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/assignee".formatted(port, savedTaskOne.getId()))
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

        restTestClient.patch()
                .uri("http://localhost:%d/api/tasks/%d/assignee".formatted(port, savedTaskOne.getId()))
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

