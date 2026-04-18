package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateUserRequest;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
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
public class UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with id: " + id)
        );

        return mapToResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest command) {
        User user = new User();
        if (command.getName() == null || command.getName().isEmpty() || command.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (command.getEmail() == null || command.getEmail().isEmpty() || command.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot find user with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
         User user = userRepository.findByEmail(email).orElseThrow(
                 () -> new EntityNotFoundException("Cannot find user with email: " + email)
         );
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);

        response.setProjectIds(new ArrayList<>());
        if (user.getProjects() != null) {
            for (Project project : user.getProjects()) {
                response.getProjectIds().add(project.getId());
            }
        }

        response.setTaskIds(new ArrayList<>());
        if (user.getTasks() != null) {
            for (Task task : user.getTasks()) {
                response.getTaskIds().add(task.getId());
            }
        }
        return response;
    }
}
