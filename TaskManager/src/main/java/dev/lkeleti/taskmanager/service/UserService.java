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
                .map(user -> {
                    UserResponse response = modelMapper.map(user, UserResponse.class);

                    response.setProjectIds(
                            user.getProjects()
                                    .stream()
                                    .map(Project::getId)
                                    .toList()
                    );

                    response.setTaskIds(
                            user.getTasks()
                                    .stream()
                                    .map(Task::getId)
                                    .toList()
                    );

                    return response;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with id: " + id)
        );

        UserResponse response = modelMapper.map(user, UserResponse.class);

        response.setProjectIds(
                user.getProjects()
                        .stream()
                        .map(Project::getId)
                        .toList()
        );

        response.setTaskIds(
                user.getTasks()
                        .stream()
                        .map(Task::getId)
                        .toList()
        );

        return response;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest command) {
        User user = new User();
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("A törölni kívánt felhasználó nem létezik.");
        }
        userRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
         User user = userRepository.findByEmail(email).orElseThrow(
                 () -> new EntityNotFoundException("Cannot find user with email: " + email)
         );
        UserResponse response = modelMapper.map(user, UserResponse.class);

        response.setProjectIds(
                user.getProjects()
                        .stream()
                        .map(Project::getId)
                        .toList()
        );

        response.setTaskIds(
                user.getTasks()
                        .stream()
                        .map(Task::getId)
                        .toList()
        );

        return response;
    }
}
