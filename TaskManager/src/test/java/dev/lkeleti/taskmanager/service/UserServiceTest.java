package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateUserRequest;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Status;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ValidationErrorException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test User Service")

class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserService userService;

    private User savedUserOne;
    private User savedUserTwo;
    private UserResponse userOneResponse;
    private UserResponse userTwoResponse;
    private final Long EXISTING_USER_ONE_ID =  1L;
    private final Long EXISTING_USER_TWO_ID = 2L;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project(100L,"Project 1", "This is project 1", LocalDateTime.now(), new ArrayList<>());
        task = new Task(200L,"Task 1", "This is Task 1", Status.TODO, LocalDate.now(), LocalDateTime.now(), null,project);
        savedUserOne = new User(EXISTING_USER_ONE_ID,"John Doe", "John.Doe@email.com", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        savedUserTwo = new User(EXISTING_USER_TWO_ID,"Joe Doe", "Joe.Doe@email.com", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());

        userOneResponse = new UserResponse(EXISTING_USER_ONE_ID,"John Doe", "John.Doe@email.com", LocalDateTime.now(), List.of(100L), List.of(200L));
        userTwoResponse = new UserResponse(EXISTING_USER_TWO_ID,"Joe Doe", "Joe.Doe@email.com", LocalDateTime.now(), null, null);
    }

    @Test
    @DisplayName("Find all users successfully")
    void testFindAllUsers_Success() {
        project.getUsers().add(savedUserOne);
        task.setAssignee(savedUserOne);
        savedUserOne.getTasks().add(task);
        savedUserOne.getProjects().add(project);

        // Arrange
        when(userRepository.findAll())
                .thenReturn(List.of(savedUserOne, savedUserTwo));

        when(modelMapper.map(savedUserOne, UserResponse.class))
                .thenReturn(userOneResponse);

        when(modelMapper.map(savedUserTwo, UserResponse.class))
                .thenReturn(userTwoResponse);

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(EXISTING_USER_ONE_ID, result.get(0).getId());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("John.Doe@email.com", result.get(0).getEmail());
        assertEquals(100L, result.get(0).getProjectIds().getFirst());
        assertEquals(200L, result.get(0).getTaskIds().getFirst());

        assertEquals(EXISTING_USER_TWO_ID, result.get(1).getId());
        assertEquals("Joe Doe", result.get(1).getName());
        assertEquals("Joe.Doe@email.com", result.get(1).getEmail());

        verify(userRepository).findAll();
        verify(modelMapper, times(2)).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Find all users successfully")
    void testFindAllUsersEmpty_Success() {
        // Arrange
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Find all users, no projects and no tasks successfully")
    void testFindAllUsersNoProjectNoTask_Success() {
        // Arrange
        when(userRepository.findAll())
                .thenReturn(List.of(savedUserOne, savedUserTwo));

        when(modelMapper.map(savedUserOne, UserResponse.class))
                .thenReturn(userOneResponse);

        when(modelMapper.map(savedUserTwo, UserResponse.class))
                .thenReturn(userTwoResponse);

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(EXISTING_USER_ONE_ID, result.get(0).getId());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("John.Doe@email.com", result.get(0).getEmail());
        assertEquals(0, result.get(0).getProjectIds().size());
        assertEquals(0, result.get(0).getTaskIds().size());

        assertEquals(EXISTING_USER_TWO_ID, result.get(1).getId());
        assertEquals("Joe Doe", result.get(1).getName());
        assertEquals("Joe.Doe@email.com", result.get(1).getEmail());
        assertEquals(0, result.get(1).getProjectIds().size());
        assertEquals(0, result.get(1).getTaskIds().size());

        verify(userRepository).findAll();
        verify(modelMapper, times(2)).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Find user by ID successfully")
    void testFindUserById_Success() {

        // Arrange (Előkészítés)
        when(userRepository.findById(EXISTING_USER_ONE_ID))
                .thenReturn(Optional.of(savedUserOne));

        when(modelMapper.map(any(User.class), eq(UserResponse.class)))
                .thenReturn(userOneResponse);

        // Act (Végrehajtás)
        UserResponse result = userService.getUserById(EXISTING_USER_ONE_ID);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_USER_ONE_ID, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("John.Doe@email.com", result.getEmail());
        assertEquals(0, result.getProjectIds().size());
        assertEquals(0, result.getTaskIds().size());

        verify(userRepository).findById(EXISTING_USER_ONE_ID);
        verify(modelMapper).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Find user by non exists ID error")
    void testFindUserById_Error() {

        // Arrange (Előkészítés)
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find user with id: 999" , exception.getMessage());

        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Find user by ID with empty task and project success")
    void testGetUserById_NullCollections_Success() {
        savedUserOne.setProjects(null);
        savedUserOne.setTasks(null);

        when(userRepository.findById(EXISTING_USER_ONE_ID))
                .thenReturn(Optional.of(savedUserOne));

        when(modelMapper.map(any(), eq(UserResponse.class)))
                .thenReturn(new UserResponse());

        UserResponse result = userService.getUserById(EXISTING_USER_ONE_ID);

        assertNotNull(result.getProjectIds());
        assertTrue(result.getProjectIds().isEmpty());

        assertNotNull(result.getTaskIds());
        assertTrue(result.getTaskIds().isEmpty());

        verify(userRepository).findById(EXISTING_USER_ONE_ID);
        verify(modelMapper).map(any(), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Create new user successfully")
    void testCreateUser_Success() {

        // Arrange (Előkészítés)
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUserOne);

        when(modelMapper.map(any(User.class), eq(UserResponse.class)))
                .thenReturn(userOneResponse);

        // Act (Végrehajtás)
        UserResponse result = userService.createUser(new CreateUserRequest("John Doe", "John.Doe@email.com"));

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_USER_ONE_ID, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("John.Doe@email.com", result.getEmail());

        verify(userRepository).save(any(User.class));
        verify(modelMapper).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Create new user name is null error")
    void testCreateUserNameNull_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest(null, "John.Doe@email.com"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Create new user name is empty error")
    void testCreateUserNameEmpty_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest("", "John.Doe@email.com"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Create new user name is whitespace error")
    void testCreateUserNameWhitespace_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest("   ", "John.Doe@email.com"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Create new user email is null error")
    void testCreateUserEmailNull_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest("John Doe", null));
        });


        // Assert (Ellenőrzés)
        assertEquals("Email is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Create new user email is empty error")
    void testCreateUserEmailEmpty_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest("John Doe", ""));
        });


        // Assert (Ellenőrzés)
        assertEquals("Email is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Create new user email is whitespace error")
    void testCreateUserEmailWhitespace_Error() {

        // Arrange (Előkészítés)

        // Act (Végrehajtás)
        ValidationErrorException exception  = assertThrows(ValidationErrorException.class, () -> {
            userService.createUser(new CreateUserRequest("John Doe", "   "));
        });


        // Assert (Ellenőrzés)
        assertEquals("Email is required", exception.getMessage());

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Delete user successfully")
    void testDeleteUser_Success() {
        // Arrange (Előkészítés)
        when(userRepository.existsById(EXISTING_USER_ONE_ID))
                .thenReturn(true);
        doNothing().when(userRepository).deleteById(EXISTING_USER_ONE_ID);

        // Act (Végrehajtás)
        userService.deleteUser(EXISTING_USER_ONE_ID);

        // Assert (Ellenőrzés)
        verify(userRepository).existsById(EXISTING_USER_ONE_ID);
        verify(userRepository).deleteById(EXISTING_USER_ONE_ID);
    }

    @Test
    @DisplayName("Delete non exists user error")
    void testDeleteNonExistsUser_Error() {

        // Arrange
        when(userRepository.existsById(999L))
                .thenReturn(false);

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        // Assert
        assertEquals("Cannot find user with id: " + 999L, exception.getMessage());

        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Find user by email successfully")
    void testFindUserByEmail_Success() {

        // Arrange (Előkészítés)
        when(userRepository.findByEmail(savedUserOne.getEmail()))
                .thenReturn(Optional.of(savedUserOne));

        when(modelMapper.map(any(User.class), eq(UserResponse.class)))
                .thenReturn(userOneResponse);

        // Act (Végrehajtás)
        UserResponse result = userService.getUserByEmail(savedUserOne.getEmail());

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_USER_ONE_ID, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("John.Doe@email.com", result.getEmail());
        assertEquals(0, result.getProjectIds().size());
        assertEquals(0, result.getTaskIds().size());

        verify(userRepository).findByEmail(savedUserOne.getEmail());
        verify(modelMapper).map(any(User.class), eq(UserResponse.class));
    }

    @Test
    @DisplayName("Find user by non exists email error")
    void testFindUserByEmail_Error() {

        // Arrange (Előkészítés)
        when(userRepository.findByEmail("nonexists@email.com"))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserByEmail("nonexists@email.com");
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find user with email: nonexists@email.com", exception.getMessage());

        verify(userRepository).findByEmail("nonexists@email.com");
    }
}
