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
import dev.lkeleti.taskmanager.repository.ProjectRepository;
import dev.lkeleti.taskmanager.repository.TaskRepository;
import dev.lkeleti.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test Task Service")
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private TaskService taskService;

    private CreateTaskRequest createTaskWithoutUserRequest;
    private CreateTaskRequest createTaskWithUserRequest;
    private Task savedTaskWithoutUser;
    private Task savedTaskWithUser;
    private TaskResponse taskWithoutUserResponse;
    private TaskResponse taskWithUserResponse;
    private Project projectWithoutUser;
    private Project projectWithUser;
    private User user;

    private final Long EXISTING_TASK_ID = 1L;
    private final Long EXISTING_PROJECT_ID = 10L;
    private final Long EXISTING_USER_ID = 100L;

    private final Long NON_EXISTING_TASK_ID = 9L;
    private final Long NON_EXISTING_PROJECT_ID = 90L;
    private final Long NON_EXISTING_USER_ID = 900L;

    @BeforeEach
    void setUp() {
        createTaskWithoutUserRequest = new CreateTaskRequest("Task_01","This is task 01",LocalDate.now().plusDays(1), null,EXISTING_PROJECT_ID);
        projectWithoutUser = new Project(EXISTING_PROJECT_ID,"Project_01","This is project 01",LocalDateTime.now(),null);
        savedTaskWithoutUser = new Task(EXISTING_TASK_ID,"Task_01","This is task 01", Status.TODO, LocalDate.now().plusDays(1), LocalDateTime.now(),null,projectWithoutUser);
        taskWithoutUserResponse = new TaskResponse(EXISTING_TASK_ID,"Task_01","This is task 01", Status.TODO, LocalDate.now().plusDays(1), LocalDateTime.now(),null, EXISTING_PROJECT_ID);
        projectWithUser = new Project(EXISTING_PROJECT_ID,"Project_01","This is project 01",LocalDateTime.now(),new ArrayList<>());
        user = new User(EXISTING_USER_ID, "John Doe", "John.Doe@email.com", LocalDateTime.now(), List.of(projectWithUser), new ArrayList<>());
        projectWithUser.getUsers().add(user);
        createTaskWithUserRequest = new CreateTaskRequest("Task_01","This is task 01",LocalDate.now().plusDays(1), user.getId(), EXISTING_PROJECT_ID);
        savedTaskWithUser = new Task(EXISTING_TASK_ID,"Task_01","This is task 01", Status.TODO, LocalDate.now().plusDays(1), LocalDateTime.now(), user, projectWithUser);
        taskWithUserResponse = new TaskResponse(EXISTING_TASK_ID,"Task_01","This is task 01", Status.TODO, LocalDate.now().plusDays(1), LocalDateTime.now(), user.getId(), EXISTING_PROJECT_ID);
    }

    @Test
    @DisplayName("Create new task successfully, without user")
    void testCreateTaskWithoutUser_Success() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(EXISTING_PROJECT_ID))
                .thenReturn(Optional.of(projectWithoutUser));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithoutUser);

        when(modelMapper.map(any(Task.class), eq(TaskResponse.class)))
                .thenReturn(taskWithoutUserResponse);

        // Act (Végrehajtás)
        TaskResponse result = taskService.createTask(createTaskWithoutUserRequest);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_TASK_ID, result.getId());
        assertEquals("Task_01", result.getTitle());
        assertEquals(EXISTING_PROJECT_ID, result.getProjectId());
        assertNull(result.getAssigneeId());
        assertEquals(Status.TODO, result.getStatus());

        verify(taskRepository).save(any(Task.class));
        verify(modelMapper).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    @DisplayName("Create new task successfully, with user")
    void testCreateTaskWithUser_Success() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(EXISTING_PROJECT_ID))
                .thenReturn(Optional.of(projectWithUser));

        when(userRepository.findById(EXISTING_USER_ID))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        when(modelMapper.map(any(Task.class), eq(TaskResponse.class)))
                .thenReturn(taskWithUserResponse);

        // Act (Végrehajtás)
        TaskResponse result = taskService.createTask(createTaskWithUserRequest);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_TASK_ID, result.getId());
        assertEquals("Task_01", result.getTitle());
        assertEquals(EXISTING_PROJECT_ID, result.getProjectId());
        assertEquals(EXISTING_USER_ID, result.getAssigneeId());
        assertEquals(Status.TODO, result.getStatus());

        verify(taskRepository).save(any(Task.class));
        verify(modelMapper).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    @DisplayName("Create new task with user error, project not exists")
    void testCreateTaskWithUserNoProject_Error() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(NON_EXISTING_PROJECT_ID))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        createTaskWithUserRequest.setProjectId(NON_EXISTING_PROJECT_ID);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(createTaskWithUserRequest);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find project with id: " + NON_EXISTING_PROJECT_ID, exception.getMessage());
        verify(projectRepository).findById(NON_EXISTING_PROJECT_ID);
    }

    @Test
    @DisplayName("Create new task without user error, project not exists")
    void testCreateTaskWithoutUserNoProject_Error() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(NON_EXISTING_PROJECT_ID))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        createTaskWithoutUserRequest.setProjectId(NON_EXISTING_PROJECT_ID);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(createTaskWithoutUserRequest);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find project with id: " + NON_EXISTING_PROJECT_ID, exception.getMessage());
        verify(projectRepository).findById(NON_EXISTING_PROJECT_ID);
    }

    @Test
    @DisplayName("Create new task with user error, user not exists")
    void testCreateTaskWithUserNoUser_Error() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(EXISTING_PROJECT_ID))
                .thenReturn(Optional.of(projectWithUser));

        when(userRepository.findById(NON_EXISTING_USER_ID))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        createTaskWithUserRequest.setAssigneeId(NON_EXISTING_USER_ID);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(createTaskWithUserRequest);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find user with id: " + NON_EXISTING_USER_ID, exception.getMessage());
        verify(projectRepository).findById(EXISTING_PROJECT_ID);
        verify(userRepository).findById(NON_EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Create new task with user error, user not part of project")
    void testCreateTaskWithUserNotPartOFProject_Error() {

        // Arrange (Előkészítés)
        projectWithoutUser.setUsers(new ArrayList<>());
        when(projectRepository.findById(EXISTING_PROJECT_ID))
                .thenReturn(Optional.of(projectWithoutUser));

        when(userRepository.findById(EXISTING_USER_ID))
                .thenReturn(Optional.of(user));

        // Act (Végrehajtás)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(createTaskWithUserRequest);
        });

        // Assert (Ellenőrzés)
        assertEquals("userId=" + EXISTING_USER_ID + " is not part of projectId=" + EXISTING_PROJECT_ID, exception.getMessage());
        verify(projectRepository).findById(EXISTING_PROJECT_ID);
        verify(userRepository).findById(EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Find task by ID successfully")
    void testFindTaskById_Success() {

        // Arrange (Előkészítés)
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));

        when(modelMapper.map(any(Task.class), eq(TaskResponse.class)))
                .thenReturn(taskWithUserResponse);

        // Act (Végrehajtás)
        TaskResponse result = taskService.getTaskById(EXISTING_TASK_ID);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_TASK_ID, result.getId());
        assertEquals("Task_01", result.getTitle());
        assertEquals(EXISTING_PROJECT_ID, result.getProjectId());
        assertEquals(EXISTING_USER_ID,result.getAssigneeId());
        assertEquals(Status.TODO, result.getStatus());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(modelMapper).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    @DisplayName("Find task by non exists ID error")
    void testFindTaskById_Error() {

        // Arrange (Előkészítés)
        when(taskRepository.findById(NON_EXISTING_TASK_ID))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTaskById(NON_EXISTING_TASK_ID);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find task with id: " + NON_EXISTING_TASK_ID, exception.getMessage());

        verify(taskRepository).findById(NON_EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Find all tasks successfully")
    void testFindAllTasks_Success() {

        // Arrange
        when(taskRepository.findAll())
                .thenReturn(List.of(savedTaskWithUser, savedTaskWithoutUser));

        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        when(modelMapper.map(savedTaskWithoutUser, TaskResponse.class))
                .thenReturn(taskWithoutUserResponse);

        // Act
        List<TaskResponse> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(EXISTING_PROJECT_ID, result.get(0).getProjectId());
        assertEquals(EXISTING_USER_ID, result.get(0).getAssigneeId());

        assertEquals(EXISTING_PROJECT_ID, result.get(1).getProjectId());
        assertNull(result.get(1).getAssigneeId());

        verify(taskRepository).findAll();
        verify(modelMapper, times(2)).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    @DisplayName("Find all tasks(empty) successfully")
    void testFindAllTasksEmpty_Success() {

        // Arrange
        when(taskRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<TaskResponse> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Update task successfully")
    void testUpdateTask_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        savedTaskWithUser.setTitle("Updated Title");
        savedTaskWithUser.setDescription("Updated Description");
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        taskWithUserResponse.setTitle("Updated Title");
        taskWithUserResponse.setDescription("Updated Description");
        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest("Updated Title","Updated Description", savedTaskWithUser.getDueDate()));

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task no change successfully")
    void testUpdateTaskNoChange_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest(savedTaskWithUser.getTitle(),savedTaskWithUser.getDescription(), savedTaskWithUser.getDueDate()));

        // Assert
        assertNotNull(result);
        assertEquals("Task_01", result.getTitle());
        assertEquals("This is task 01", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task partial title success")
    void testUpdateTaskPartialTitle_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        savedTaskWithUser.setTitle("Updated Title");

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        taskWithUserResponse.setTitle("Updated Title");
        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest("Updated Title",null, savedTaskWithUser.getDueDate()));
        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("This is task 01", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task partial description success")
    void testUpdateTaskPartialDesc_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        savedTaskWithUser.setDescription("Updated Description");
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        taskWithUserResponse.setDescription("Updated Description");
        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest(null,"Updated Description", savedTaskWithUser.getDueDate()));

        // Assert
        assertNotNull(result);
        assertEquals("Task_01", result.getTitle());
        assertEquals("Updated Description", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task partial dueDate success")
    void testUpdateTaskPartialDue_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        savedTaskWithUser.setTitle("Updated Title");
        savedTaskWithUser.setDescription("Updated Description");
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        taskWithUserResponse.setTitle("Updated Title");
        taskWithUserResponse.setDescription("Updated Description");
        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest("Updated Title","Updated Description", null));

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(savedTaskWithUser.getDueDate(), result.getDueDate());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task with null success")
    void testUpdateTaskNull_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.updateTask(EXISTING_TASK_ID,new UpdateTaskRequest());

        // Assert
        assertNotNull(result);
        assertEquals("Task_01", result.getTitle());
        assertEquals("This is task 01", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Update task by non exists ID error")
    void testUpdateTaskById_Error() {

        // Arrange (Előkészítés)
        when(taskRepository.findById(NON_EXISTING_TASK_ID))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(NON_EXISTING_TASK_ID,new UpdateTaskRequest());
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find task with id: " + NON_EXISTING_TASK_ID, exception.getMessage());

        verify(taskRepository).findById(NON_EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Delete task successfully")
    void testDeleteTask_Success() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));
        doNothing().when(taskRepository).delete(any(Task.class));

        // Act
        taskService.deleteTask(EXISTING_TASK_ID);

        // Assert
        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).delete(any(Task.class));
    }

    @Test
    @DisplayName("Delete non exists task error")
    void testDeleteNonExistsTask_Error() {

        // Arrange
        when(taskRepository.findById(NON_EXISTING_TASK_ID))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.deleteTask(NON_EXISTING_TASK_ID);
        });

        // Assert
        assertEquals("Cannot find task with id: " + NON_EXISTING_TASK_ID, exception.getMessage());

        verify(taskRepository).findById(NON_EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Change status successfully")
    void testChangeStatus_Success() {
        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));

        Task newProgress = new Task(EXISTING_TASK_ID,"Task_01","This is task 01", Status.IN_PROGRESS, LocalDate.now().plusDays(1), LocalDateTime.now(), user, projectWithoutUser);
        when(taskRepository.save(any(Task.class)))
                .thenReturn(newProgress);

        TaskResponse newResponse = new TaskResponse(EXISTING_TASK_ID,"Task_01","This is task 01", Status.IN_PROGRESS, LocalDate.now().plusDays(1), LocalDateTime.now(), user.getId(), EXISTING_PROJECT_ID);
        when(modelMapper.map(newProgress, TaskResponse.class))
                .thenReturn(newResponse);

        // Act
        TaskResponse result = taskService.changeStatus(EXISTING_TASK_ID,new UpdateTaskStatusRequest(Status.IN_PROGRESS));

        // Assert
        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.getStatus());
        assertEquals("Task_01", result.getTitle());
        assertEquals("This is task 01", result.getDescription());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Change status of non exists task error")
    void testChangeStatusNonExistsTask_Error() {

        // Arrange
        when(taskRepository.findById(NON_EXISTING_TASK_ID))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.changeStatus(NON_EXISTING_TASK_ID,new UpdateTaskStatusRequest());
        });

        // Assert
        assertEquals("Cannot find task with id: " + NON_EXISTING_TASK_ID, exception.getMessage());

        verify(taskRepository).findById(NON_EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Change status to invalid error")
    void testChangeStatusToInvalid_Error() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));


        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.changeStatus(EXISTING_TASK_ID,new UpdateTaskStatusRequest(Status.DONE));
        });

        // Assert
        assertEquals("Cannot change status of task to status: " + Status.DONE, exception.getMessage());

        verify(taskRepository).findById(EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Change status to the same status error")
    void testChangeStatusToSame_Error() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));


        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.changeStatus(EXISTING_TASK_ID,new UpdateTaskStatusRequest(Status.TODO));
        });

        // Assert
        assertEquals("Cannot change status of task to status: " + Status.TODO, exception.getMessage());

        verify(taskRepository).findById(EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Change status to null error")
    void testChangeStatusToNull_Error() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));


        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.changeStatus(EXISTING_TASK_ID,new UpdateTaskStatusRequest(null));
        });

        // Assert
        assertEquals("Cannot change status of task to null", exception.getMessage());

        verify(taskRepository).findById(EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Assign user to task successfully")
    void testAssignUser_Success() {
        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));

        when(userRepository.findById(EXISTING_USER_ID))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTaskWithUser);

        when(modelMapper.map(savedTaskWithUser, TaskResponse.class))
                .thenReturn(taskWithUserResponse);

        // Act
        TaskResponse result = taskService.assigneeTask(EXISTING_TASK_ID,new AssigneeTaskRequest(EXISTING_USER_ID));

        // Assert
        assertNotNull(result);
        assertEquals(Status.TODO, result.getStatus());
        assertEquals("Task_01", result.getTitle());
        assertEquals("This is task 01", result.getDescription());
        assertEquals(EXISTING_USER_ID, result.getAssigneeId());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(userRepository).findById(EXISTING_USER_ID);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Assign user to non exists task error")
    void testAssignUserToNonExistsTask_Error() {

        // Arrange
        when(taskRepository.findById(NON_EXISTING_TASK_ID))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.assigneeTask(NON_EXISTING_TASK_ID,new AssigneeTaskRequest(EXISTING_USER_ID));
        });

        // Assert
        assertEquals("Cannot find task with id: " + NON_EXISTING_TASK_ID, exception.getMessage());

        verify(taskRepository).findById(NON_EXISTING_TASK_ID);
    }

    @Test
    @DisplayName("Assign non exists user to task error")
    void testAssignNONExistsUserToTask_Error() {

        // Arrange
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));

        when(userRepository.findById(NON_EXISTING_USER_ID))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.assigneeTask(EXISTING_TASK_ID,new AssigneeTaskRequest(NON_EXISTING_USER_ID));
        });

        // Assert
        assertEquals("Cannot find user with id: " + NON_EXISTING_USER_ID, exception.getMessage());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(userRepository).findById(NON_EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Assign user to task not member of project error")
    void testAssignUserToTaskNotMemberOfProject_Error() {

        // Arrange
        savedTaskWithUser.getProject().setUsers(new ArrayList<>());
        when(taskRepository.findById(EXISTING_TASK_ID))
                .thenReturn(Optional.of(savedTaskWithUser));

        when(userRepository.findById(EXISTING_USER_ID))
                .thenReturn(Optional.of(user));

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.assigneeTask(EXISTING_TASK_ID,new AssigneeTaskRequest(EXISTING_USER_ID));
        });

        // Assert
        assertEquals("Cannot assign task to user, because user not part of the project. user: " + EXISTING_USER_ID, exception.getMessage());

        verify(taskRepository).findById(EXISTING_TASK_ID);
        verify(userRepository).findById(EXISTING_USER_ID);
    }
}

