package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateProjectRequest;
import dev.lkeleti.taskmanager.dto.response.ProjectResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Status;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Project Service")
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ProjectService projectService;

    private Project savedProjectOne;
    private Project savedProjectTwo;
    private ProjectResponse projectOneResponse;
    private ProjectResponse projectTwoResponse;
    private final Long EXISTING_PROJECT_ONE_ID =  1L;
    private final Long EXISTING_PROJECT_TWO_ID = 2L;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User(100L,"John Doe", "John.Doe@email.com", LocalDateTime.now(), new ArrayList<>(), new ArrayList<>());
        task = new Task(200L,"Task 1", "This is Task 1", Status.TODO, LocalDate.now(), LocalDateTime.now(), user,null);
        savedProjectOne = new Project(EXISTING_PROJECT_ONE_ID,"Project 01", "This is project 1", LocalDateTime.now(), new ArrayList<>());
        savedProjectTwo = new Project(EXISTING_PROJECT_TWO_ID,"Project 02", "This is project 2",LocalDateTime.now(),new ArrayList<>());

        projectOneResponse = new ProjectResponse(EXISTING_PROJECT_ONE_ID,"Project 01", "This is project 1",LocalDateTime.now(),new ArrayList<>());
        projectTwoResponse = new ProjectResponse(EXISTING_PROJECT_TWO_ID,"Project 02", "This is project 2",LocalDateTime.now(),new ArrayList<>());
    }

    @Test
    @DisplayName("Find all projects successfully")
    void testFindAllProjects_Success() {

        savedProjectOne.getUsers().add(user);
        projectOneResponse.getUserIds().add(user.getId());
        user.getProjects().add(savedProjectOne);
        user.getProjects().add(savedProjectTwo);

        // Arrange
        when(projectRepository.findAll())
                .thenReturn(List.of(savedProjectOne, savedProjectTwo));

        when(modelMapper.map(savedProjectOne, ProjectResponse.class))
                .thenReturn(projectOneResponse);

        when(modelMapper.map(savedProjectTwo, ProjectResponse.class))
                .thenReturn(projectTwoResponse);

        // Act
        List<ProjectResponse> result = projectService.getAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(EXISTING_PROJECT_ONE_ID, result.get(0).getId());
        assertEquals("Project 01", result.get(0).getName());
        assertEquals("This is project 1", result.get(0).getDescription());
        assertEquals(1, result.get(0).getUserIds().size());
        assertEquals(100L, result.get(0).getUserIds().get(0));

        assertEquals(EXISTING_PROJECT_TWO_ID, result.get(1).getId());
        assertEquals("Project 02", result.get(1).getName());
        assertEquals("This is project 2", result.get(1).getDescription());

        verify(projectRepository).findAll();
        verify(modelMapper, times(2)).map(any(Project.class), eq(ProjectResponse.class));
    }

    @Test
    @DisplayName("Find all projects, but empty successfully")
    void testFindAllProjectsEmpty_Success() {
        // Arrange
        when(projectRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<ProjectResponse> result = projectService.getAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(projectRepository).findAll();
    }

    @Test
    @DisplayName("Find all projects, no user successfully")
    void testFindAllProjectsNoUser_Success() {
        // Arrange
        when(projectRepository.findAll())
                .thenReturn(List.of(savedProjectOne, savedProjectTwo));

        when(modelMapper.map(savedProjectOne, ProjectResponse.class))
                .thenReturn(projectOneResponse);

        when(modelMapper.map(savedProjectTwo, ProjectResponse.class))
                .thenReturn(projectTwoResponse);

        // Act
        List<ProjectResponse> result = projectService.getAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(EXISTING_PROJECT_ONE_ID, result.get(0).getId());
        assertEquals("Project 01", result.get(0).getName());
        assertEquals("This is project 1", result.get(0).getDescription());
        assertEquals(0, result.get(0).getUserIds().size());

        assertEquals(EXISTING_PROJECT_TWO_ID, result.get(1).getId());
        assertEquals("Project 02", result.get(1).getName());
        assertEquals("This is project 2", result.get(1).getDescription());
        assertEquals(0, result.get(1).getUserIds().size());

        verify(projectRepository).findAll();
        verify(modelMapper, times(2)).map(any(Project.class), eq(ProjectResponse.class));
    }

    @Test
    @DisplayName("Find project by ID successfully")
    void testFindProjectById_Success() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(Optional.of(savedProjectOne));

        when(modelMapper.map(any(Project.class), eq(ProjectResponse.class)))
                .thenReturn(projectOneResponse);

        // Act (Végrehajtás)
        ProjectResponse result = projectService.getProjectById(EXISTING_PROJECT_ONE_ID);

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_PROJECT_ONE_ID, result.getId());
        assertEquals("Project 01", result.getName());
        assertEquals("This is project 1", result.getDescription());
        assertEquals(0, result.getUserIds().size());

        verify(projectRepository).findById(EXISTING_PROJECT_ONE_ID);
        verify(modelMapper).map(any(Project.class), eq(ProjectResponse.class));
    }

    @Test
    @DisplayName("Find project by non exists ID error")
    void testFindProjectById_Error() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act (Végrehajtás)
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.getProjectById(999L);
        });

        // Assert (Ellenőrzés)
        assertEquals("Cannot find project with id: 999" , exception.getMessage());

        verify(projectRepository).findById(999L);
    }

    @Test
    @DisplayName("Find project by ID with empty user success")
    void testGetProjectById_NullCollection_Success() {
        savedProjectOne.setUsers(null);

        when(projectRepository.findById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(Optional.of(savedProjectOne));

        when(modelMapper.map(any(Project.class), eq(ProjectResponse.class)))
                .thenReturn(new ProjectResponse());

        ProjectResponse result = projectService.getProjectById(EXISTING_PROJECT_ONE_ID);

        assertNotNull(result.getUserIds());
        assertTrue(result.getUserIds().isEmpty());

        verify(projectRepository).findById(EXISTING_PROJECT_ONE_ID);
        verify(modelMapper).map(any(Project.class), eq(ProjectResponse.class));
    }

    @Test
    @DisplayName("Create new project successfully")
    void testCreateProject_Success() {

        // Arrange (Előkészítés)
        when(projectRepository.save(any(Project.class)))
                .thenReturn(savedProjectOne);

        when(modelMapper.map(any(Project.class), eq(ProjectResponse.class)))
                .thenReturn(projectOneResponse);

        // Act (Végrehajtás)
        ProjectResponse result = projectService.createProject(new CreateProjectRequest("Project 01", "This is project 1"));

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_PROJECT_ONE_ID, result.getId());
        assertEquals("Project 01", result.getName());
        assertEquals("This is project 1", result.getDescription());

        verify(projectRepository).save(any(Project.class));
        verify(modelMapper).map(any(Project.class), eq(ProjectResponse.class));
    }

    @Test
    @DisplayName("Create new project name is null error")
    void testCreateUserNameNull_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest(null, "This is a new project"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Create new project name is empty error")
    void testCreateUserNameEmpty_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest("", "This is a new project"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Create new project name is whitespace error")
    void testCreateUserNameWhitespace_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest("   ", "This is a new project"));
        });


        // Assert (Ellenőrzés)
        assertEquals("Name is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Create new project description is null error")
    void testCreateUserDescriptionNull_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest("New project", null));
        });


        // Assert (Ellenőrzés)
        assertEquals("Description is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Create new project description is empty error")
    void testCreateUserDescriptionEmpty_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest("New project", ""));
        });


        // Assert (Ellenőrzés)
        assertEquals("Description is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Create new project description is whitespace error")
    void testCreateUserDescriptionWhitespace_Error() {

        // Arrange (Előkészítés)
        IllegalArgumentException exception  = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(new CreateProjectRequest("New project", "    "));
        });


        // Assert (Ellenőrzés)
        assertEquals("Description is required", exception.getMessage());

        verifyNoInteractions(projectRepository);
    }

    @Test
    @DisplayName("Delete project successfully")
    void testDeleteProject_Success() {
        // Arrange (Előkészítés)
        when(projectRepository.existsById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(true);
        doNothing().when(projectRepository).deleteById(EXISTING_PROJECT_ONE_ID);

        // Act (Végrehajtás)
        projectService.deleteProject(EXISTING_PROJECT_ONE_ID);

        // Assert (Ellenőrzés)
        verify(projectRepository).existsById(EXISTING_PROJECT_ONE_ID);
        verify(projectRepository).deleteById(EXISTING_PROJECT_ONE_ID);
    }

    @Test
    @DisplayName("Delete non exists project error")
    void testDeleteNonExistsProject_Error() {

        // Arrange
        when(projectRepository.existsById(999L))
                .thenReturn(false);

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.deleteProject(999L);
        });

        // Assert
        assertEquals("Cannot find project with id: " + 999L, exception.getMessage());

        verify(projectRepository).existsById(999L);
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Assign user to project successfully")
    void testAssignUserToProject_Success() {

        // Arrange (Előkészítés)
        when(projectRepository.findById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(Optional.of(savedProjectOne));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(any(Project.class), eq(ProjectResponse.class)))
                .thenReturn(projectOneResponse);

        // Act (Végrehajtás)
        ProjectResponse result = projectService.assignUserToProject(EXISTING_PROJECT_ONE_ID,user.getId());

        // Assert (Ellenőrzés)
        assertNotNull(result);
        assertEquals(EXISTING_PROJECT_ONE_ID, result.getId());
        assertEquals("Project 01", result.getName());
        assertEquals("This is project 1", result.getDescription());
        assertEquals(1, result.getUserIds().size());
        assertEquals(user.getId(), result.getUserIds().get(0));

        assertTrue(savedProjectOne.getUsers().contains(user));
        assertTrue(user.getProjects().contains(savedProjectOne));


        verify(projectRepository).findById(EXISTING_PROJECT_ONE_ID);
        verify(userRepository).findById(user.getId());
        verify(projectRepository).save(any(Project.class));
        verify(modelMapper).map(any(Project.class), eq(ProjectResponse.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Assign user to project already assigned error")
    void testAssignUserToProjectAlreadyAssigned_Error() {

        // Arrange (Előkészítés)
        savedProjectOne.getUsers().add(user);
        projectOneResponse.getUserIds().add(user.getId());
        when(projectRepository.findById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(Optional.of(savedProjectOne));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        // Arrange (Előkészítés)
        IllegalStateException exception  = assertThrows(IllegalStateException.class, () -> {
            projectService.assignUserToProject(EXISTING_PROJECT_ONE_ID,user.getId());
        });

        // Assert (Ellenőrzés)
        assertEquals("A felhasználó már hozzá van rendelve a projekthez.", exception.getMessage());

        verify(projectRepository).findById(EXISTING_PROJECT_ONE_ID);
        verify(userRepository).findById(user.getId());
        verify(projectRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Assign user to non exists project error")
    void testAssignUserToNonExistsProject_Error() {

        // Arrange
        when(projectRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.assignUserToProject(999L, user.getId());
        });

        // Assert
        assertEquals("Cannot find project with id: " + 999L, exception.getMessage());

        verify(projectRepository).findById(999L);
        verify(userRepository, never()).findById(user.getId());
        verify(projectRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Assign non exists user to project error")
    void testAssignNonExistsUserToProject_Error() {

        // Arrange
        when(projectRepository.findById(EXISTING_PROJECT_ONE_ID))
                .thenReturn(Optional.of(savedProjectOne));

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.assignUserToProject(EXISTING_PROJECT_ONE_ID, 999L);
        });

        // Assert
        assertEquals("Cannot find user with id: " + 999L, exception.getMessage());

        verify(projectRepository).findById(EXISTING_PROJECT_ONE_ID);
        verify(userRepository).findById(999L);
        verify(projectRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}
