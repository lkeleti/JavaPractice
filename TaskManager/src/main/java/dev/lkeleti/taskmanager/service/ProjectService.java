package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateProjectRequest;
import dev.lkeleti.taskmanager.dto.response.ProjectResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
import dev.lkeleti.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find project with id: " + id)
        );
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest command) {
        Project project = new Project();
        if (command.getName() == null || command.getName().isEmpty() || command.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (command.getDescription() == null || command.getDescription().isEmpty() || command.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        project.setName(command.getName());
        project.setDescription(command.getDescription());
        project.setUsers(new ArrayList<>());
        return mapToResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot find project with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Transactional
    public ProjectResponse assignUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find project with id: " + projectId)
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with id: " + userId)
        );

        if (project.getUsers() != null && project.getUsers().contains(user)) {
            throw new IllegalStateException("A felhasználó már hozzá van rendelve a projekthez.");
        }

        if (project.getUsers() == null) {
            project.setUsers(new ArrayList<>());
        }

        if (user.getProjects() == null) {
            user.setProjects(new ArrayList<>());
        }

        project.getUsers().add(user);
        user.getProjects().add(project);
        projectRepository.save(project);
        userRepository.save(user);

        return mapToResponse(project);
    }
    
    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse response = modelMapper.map(project, ProjectResponse.class);

        response.setUserIds(new ArrayList<>());
        if (project.getUsers() != null) {
            for (User user : project.getUsers()) {
                response.getUserIds().add(user.getId());
            }
        }

        return response;
    }
}
