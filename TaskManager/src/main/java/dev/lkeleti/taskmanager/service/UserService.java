package dev.lkeleti.taskmanager.service;

import dev.lkeleti.taskmanager.dto.request.CreateUserRequest;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
import dev.lkeleti.taskmanager.entity.Project;
import dev.lkeleti.taskmanager.entity.Task;
import dev.lkeleti.taskmanager.entity.User;
import dev.lkeleti.taskmanager.exception.ValidationErrorException;
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
public class UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy) {

        log.info("Fetching users page={}, size={}, sortBy={}", page, size, sortBy);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        Page<UserResponse> result = userRepository.findAll(pageable)
                .map(this::mapToResponse);

        log.debug("Fetched {} users (total elements: {})",
                result.getNumberOfElements(),
                result.getTotalElements());

        return result;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id={}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with id={}", id);
            return new EntityNotFoundException("Cannot find user with id: " + id);
        });

        log.debug("User found: id={}, email={}", user.getId(), user.getEmail());
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest command) {
        log.debug("Creating user: {}", command);

        if (command.getName() == null || command.getName().isBlank()) {
            log.warn("User creation failed - invalid name");
            throw new ValidationErrorException("Name is required");
        }

        if (command.getEmail() == null || command.getEmail().isBlank()) {
            log.warn("User creation failed - invalid email");
            throw new ValidationErrorException("Email is required");
        }

        User user = new User();
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        user.setProjects(new ArrayList<>());
        user.setTasks(new ArrayList<>());

        User saved = userRepository.save(user);
        log.info("User created with id={}, email={}", saved.getId(), saved.getEmail());

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id={}", id);

        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion with id={}", id);
            throw new EntityNotFoundException("Cannot find user with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted with id={}", id);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email={}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User not found with email={}", email);
            return new EntityNotFoundException("Cannot find user with email: " + email);
        });

        log.debug("User found: id={}, email={}", user.getId(), user.getEmail());
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
