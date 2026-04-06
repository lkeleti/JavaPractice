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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a felhasználókkal")
public class UserController {
    private UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes felhasználó listázása",
            description = "Visszaadja az összes felhasználó listáját.")
    @ApiResponse(responseCode = "200", description = "Felhasználók sikeresen listázva")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy felhasználó lekérdezése ID alapján",
            description = "Visszaadja a megadott ID-hoz tartozó felhasználó adatait.")
    @ApiResponse(responseCode = "200", description = "Felhasználó sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Felhasználó nem található")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy felhasználó lekérdezése email alapján",
            description = "Visszaadja a megadott email-el rendelkező felhasználó adatait.")
    @ApiResponse(responseCode = "200", description = "Felhasználó sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Felhasználó nem található")
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
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
        return userService.createUser(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Felhasználó törlése",
            description = "Felhasználó törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Felhasználó sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő felhasználó nem található")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
