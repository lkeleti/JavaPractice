package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.CreateUserRequest;
import dev.lkeleti.taskmanager.dto.response.UserResponse;
import dev.lkeleti.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Slf4j
@Tag(name = "Műveletek a felhasználókkal")
public class UserController {
    private UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes felhasználó listázása",
            description = "Visszaadja az összes felhasználó listáját.")
    @ApiResponse(responseCode = "200", description = "Felhasználók sikeresen listázva")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {

        log.info("GET /users - Fetching all users");
        Page<UserResponse> result = userService.getAllUsers(page, size, sortBy);
        log.debug("Fetched {} users (total elements: {})",
                result.getNumberOfElements(),
                result.getTotalElements());
        return result;
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy felhasználó lekérdezése ID alapján",
            description = "Visszaadja a megadott ID-hoz tartozó felhasználó adatait.")
    @ApiResponse(responseCode = "200", description = "Felhasználó sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Felhasználó nem található")
    public UserResponse getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Fetching user by id", id);
        UserResponse result = userService.getUserById(id);
        log.debug("GET /users/{} - User found", id);
        return result;
    }

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy felhasználó lekérdezése email alapján",
            description = "Visszaadja a megadott email-el rendelkező felhasználó adatait.")
    @ApiResponse(responseCode = "200", description = "Felhasználó sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Felhasználó nem található")
    public UserResponse getUserByEmail(@PathVariable String email) {
        log.info("GET /users/email/{} - Fetching user by email", email);
        UserResponse result = userService.getUserByEmail(email);
        log.debug("GET /users/email/{} - User found", email);
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új felhasználó rögzítése",
            description = "Új felhasználó rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új felhasználó létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateUserRequest.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Felhasználó sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest command) {
        log.info("POST /users - Creating user with email={}", command.getEmail());
        UserResponse result = userService.createUser(command);
        log.info("POST /users - User created with id={}", result.getId());
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Felhasználó törlése",
            description = "Felhasználó törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Felhasználó sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő felhasználó nem található")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        userService.deleteUser(id);
        log.info("DELETE /users/{} - User deleted", id);
    }

}
