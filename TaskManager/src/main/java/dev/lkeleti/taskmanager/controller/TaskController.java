package dev.lkeleti.taskmanager.controller;

import dev.lkeleti.taskmanager.dto.request.AssigneeTaskRequest;
import dev.lkeleti.taskmanager.dto.request.CreateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskRequest;
import dev.lkeleti.taskmanager.dto.request.UpdateTaskStatusRequest;
import dev.lkeleti.taskmanager.dto.response.TaskResponse;
import dev.lkeleti.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Slf4j
@Tag(name = "Műveletek a feladatokkal")
public class TaskController {
    private TaskService taskService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes feladat listázása",
            description = "Visszaadja az összes feladat listáját.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feladatok sikeresen listázva"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<TaskResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        log.info("GET /tasks - Fetching all tasks");
        Page<TaskResponse> result = taskService.getAllTasks(page, size, sortBy);
        log.debug("Fetched {} tasks (total elements: {})",
                result.getNumberOfElements(),
                result.getTotalElements());
        return result;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy feladat lekérdezése ID alapján",
            description = "Visszaadja a megadott ID-hoz tartozó feladat adatait.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feladat sikeresen lekérdezve"),
            @ApiResponse(responseCode = "404", description = "Feladat nem található"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TaskResponse getTaskById(@PathVariable Long id) {
        log.info("GET /tasks/{} - Fetching task by id", id);
        TaskResponse result = taskService.getTaskById(id);
        log.debug("GET /tasks/{} - Task found", id);
        return result;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új feladat rögzítése",
            description = "Új feladat rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új feladat létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateTaskRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feladat sikeresen létrehozva"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest command) {
        log.info("POST /tasks - Creating task with title={}", command.getTitle());
        TaskResponse result = taskService.createTask(command);
        log.info("POST /tasks - Task created with id={}", result.getId());
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Feladat törlése",
            description = "Feladat törlése a megadott azonosító alapján."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feladat sikeresen törölve"),
            @ApiResponse(responseCode = "404", description = "Törlendő feladat nem található"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        log.info("DELETE /tasks/{} - Task deleted", id);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Feladat módosítása",
            description = "Feladat módosítása a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A feladat módosításához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feladat sikeresen módosítva"),
            @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest command) {
        log.info("PUT /tasks/{} - Updating task", id);
        TaskResponse result = taskService.updateTask(id, command);
        log.info("PUT /tasks/{} - Task updated", id);
        return result;
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Feladat státuszának módosítása",
            description = "Feladat státuszának módosítása a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A feladat státuszának módosításához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskStatusRequest.class))
            )
    )@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feladat státusza sikeresen módosítva"),
            @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse changeStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest command) {
        log.info("PATCH /tasks/{}/status - Changing status to {}", id, command.getStatus());
        TaskResponse result = taskService.changeStatus(id, command);
        log.info("PATCH /tasks/{}/status - Status changed", id);
        return result;
    }

    @PatchMapping("/{id}/assignee")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Feladathoz felhasználó rendelése",
            description = "Feladathoz felhasználó rendelése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A feladathoz felhasználó rendeléséhez szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssigneeTaskRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Felhasználó feladathoz rendelése sikeresen módosítva"),
            @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)"),
            @ApiResponse(responseCode = "401", description = "Nem authentikált felhasználó"),
            @ApiResponse(responseCode = "403", description = "Nincs megfelelő jogosultság")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse assigneeTask(@PathVariable Long id, @Valid @RequestBody AssigneeTaskRequest command) {
        log.info("PATCH /tasks/{}/assignee - Assigning userId={}", id, command.getAssigneeId());
        TaskResponse result = taskService.assigneeTask(id, command);
        log.info("PATCH /tasks/{}/assignee - Assignment completed", id);
        return result;
    }
}
