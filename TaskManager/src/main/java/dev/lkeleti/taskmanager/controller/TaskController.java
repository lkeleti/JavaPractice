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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a feladatokkal")
public class TaskController {
    private TaskService taskService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes feladat listázása",
            description = "Visszaadja az összes feladat listáját.")
    @ApiResponse(responseCode = "200", description = "Feladatok sikeresen listázva")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy feladat lekérdezése ID alapján",
            description = "Visszaadja a megadott ID-hoz tartozó feladat adatait.")
    @ApiResponse(responseCode = "200", description = "Feladat sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Feladat nem található")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
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
    @ApiResponse(responseCode = "201", description = "Feladat sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest command) {
        return taskService.createTask(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Feladat törlése",
            description = "Feladat törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Feladat sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő feladat nem található")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
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
    @ApiResponse(responseCode = "200", description = "Feladat sikeresen módosítva")
    @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest command) {
        return taskService.updateTask(id, command);
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
    )
    @ApiResponse(responseCode = "200", description = "Feladat státusza sikeresen módosítva")
    @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public TaskResponse changeStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest command) {
        return taskService.changeStatus(id, command);
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
    @ApiResponse(responseCode = "200", description = "Felhasználó feladathoz rendelése sikeresen módosítva")
    @ApiResponse(responseCode = "404", description = "Módosítandó feladat nem található")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public TaskResponse assigneeTask(@PathVariable Long id, @Valid @RequestBody AssigneeTaskRequest command) {
        return taskService.assigneeTask(id, command);
    }
}
