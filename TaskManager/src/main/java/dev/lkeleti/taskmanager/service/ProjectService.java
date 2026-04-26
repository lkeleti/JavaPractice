package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateProjectRequest;
import dev.lkeleti.taskmanager.dto.response.ProjectResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ValidationErrorException;
import dev.lkeleti.taskmanager.repository.ProjectRepository;
import dev.lkeleti.taskmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(int page, int size, String sortBy) {
        log.info("Fetching projects page={}, size={}, sortBy={}", page, size, sortBy);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        Page<ProjectResponse> result = projectRepository.findAll(pageable)
                .map(this::mapToResponse);

        log.debug("Fetched {} projects (total elements: {})",
                result.getNumberOfElements(),
                result.getTotalElements());

        return result;
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        log.debug("Fetching project by id={}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Project not found with id={}", id);
                    return new EntityNotFoundException("Cannot find project with id: " + id);
                });

        log.debug("Project found with id={}", id);

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest command) {
        log.debug("Creating project: {}", command);
        Project project = new Project();
        if (command.getName() == null || command.getName().isEmpty() || command.getName().isBlank()) {
            log.warn("Project creation failed - invalid name");
            throw new ValidationErrorException("Name is required");
        }
        if (command.getDescription() == null || command.getDescription().isEmpty() || command.getDescription().isBlank()) {
            log.warn("Project creation failed - invalid description");
            throw new ValidationErrorException("Description is required");
        }
        project.setName(command.getName());
        project.setDescription(command.getDescription());
        project.setUsers(new ArrayList<>());
        Project createdProject = projectRepository.save(project);
        log.info("Project saved with id={}", createdProject.getId());
        return mapToResponse(createdProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        log.debug("Deleting project with id={}", id);

        if (!projectRepository.existsById(id)) {
            log.warn("Delete failed - project not found with id={}", id);
            throw new EntityNotFoundException("Cannot find project with id: " + id);
        }

        projectRepository.deleteById(id);

        log.info("Project deleted with id={}", id);
    }

    @Transactional
    public ProjectResponse assignUserToProject(Long projectId, Long userId) {
        log.debug("Assigning user {} to project {}", userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found with id={}", projectId);
                    return new EntityNotFoundException("Cannot find project with id: " + projectId);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id={}", userId);
                    return new EntityNotFoundException("Cannot find user with id: " + userId);
                });

        if (project.getUsers() != null && project.getUsers().contains(user)) {
            log.warn("Assignment failed - user {} already assigned to project {}", userId, projectId);
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

        log.info("User {} assigned to project {}", userId, projectId);

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
