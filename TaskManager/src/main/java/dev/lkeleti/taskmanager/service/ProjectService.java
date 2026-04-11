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
                .map(project -> {
                    ProjectResponse response = modelMapper.map(project, ProjectResponse.class);

                    response.setUserIds(
                            project.getUsers()
                                    .stream()
                                    .map(User::getId)
                                    .toList()
                    );
                    return response;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find project with id: " + id)
        );
        ProjectResponse response = modelMapper.map(project, ProjectResponse.class);
        response.setUserIds(
                project.getUsers()
                        .stream()
                        .map(User::getId)
                        .toList()
        );
        return response;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest command) {
        Project project = new Project();
        project.setName(command.getName());
        project.setDescription(command.getDescription());
        return modelMapper.map(projectRepository.save(project), ProjectResponse.class);
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("A törölni kívánt projekt nem létezik.");
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

        if (project.getUsers().contains(user)) {
            throw new IllegalStateException("A felhasználó már hozzá van rendelve a projekthez.");
        }

        project.getUsers().add(user);
        user.getProjects().add(project);
        projectRepository.save(project);
        userRepository.save(user);

        ProjectResponse response = modelMapper.map(project, ProjectResponse.class);
        response.setUserIds(
                project.getUsers()
                        .stream()
                        .map(User::getId)
                        .toList()
        );
        return response;
    }

}
