package dev.lkeleti.taskmanager.controller;


import dev.lkeleti.taskmanager.dto.request.CreateProjectRequest;
import dev.lkeleti.taskmanager.dto.response.ProjectResponse;
import dev.lkeleti.taskmanager.service.ProjectService;
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
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a projektekkel")
public class ProjectController {
    private ProjectService projectService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes project listázása",
            description = "Visszaadja az összes project listáját.")
    @ApiResponse(responseCode = "200", description = "Projektek sikeresen listázva")
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy projekt lekérdezése ID alapján",
            description = "Visszaadja a megadott ID-hoz tartozó projekt adatait.")
    @ApiResponse(responseCode = "200", description = "Projekt sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Projekt nem található")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új projekt rögzítése",
            description = "Új projekt rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új projekt létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateProjectRequest.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Projekt sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest command) {
        return projectService.createProject(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Projekt törlése",
            description = "Projekt törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Projekt sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő projekt nem található")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Felhasználó projekthez rendelése",
            description = "Felhasználó projekthez rendelése a path változóban megadott adatok alapján."
    )
    @ApiResponse(responseCode = "201", description = "Fekhasználó projekthez rendelése sikeresen megtörtént")
    @ApiResponse(responseCode = "404", description = "A projekt, vagy a felhasználó nem található")
    public ProjectResponse assignUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectService.assignUserToProject(projectId, userId);
    }
}
