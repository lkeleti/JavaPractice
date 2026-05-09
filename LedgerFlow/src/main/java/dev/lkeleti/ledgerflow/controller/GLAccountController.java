package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.GLAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.GLAccountUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.GLAccountResponse;
import dev.lkeleti.ledgerflow.service.GLAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gl-accounts")
@RequiredArgsConstructor
@Tag(
        name = "GL Account API",
        description = "Főkönyvi számlák kezelése, létrehozása, módosítása, logikai törlése és visszaállítása"
)
public class GLAccountController {

    private final GLAccountService service;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Új főkönyvi számla létrehozása",
            description = "Létrehoz egy új főkönyvi számlát a megadott adatok alapján."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "A számla sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = GLAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A számla már létezik (számlaszám ütközés)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public GLAccountResponse create(
            @Valid @RequestBody GLAccountCreateRequest request
    ) {
        return service.create(request);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Főkönyvi számla lekérése ID alapján",
            description = "Visszaadja a megadott azonosítójú aktív főkönyvi számlát."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = GLAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található vagy törölt",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public GLAccountResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ---------------------------------------------------------
    // GET ALL (only active)
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes aktív főkönyvi számla lekérése",
            description = "Visszaadja az összes aktív (nem törölt) főkönyvi számlát."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = GLAccountResponse.class))
            )
    )
    public List<GLAccountResponse> getAll() {
        return service.getAll();
    }

    // ---------------------------------------------------------
    // GET ALL INCLUDING DELETED
    // ---------------------------------------------------------
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes főkönyvi számla lekérése (töröltekkel együtt)",
            description = "Visszaadja az összes főkönyvi számlát, beleértve a logikailag törölteket is."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = GLAccountResponse.class))
            )
    )
    public List<GLAccountResponse> getAllIncludingDeleted() {
        return service.getAllIncludingDeleted();
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Főkönyvi számla módosítása",
            description = "Frissíti a megadott azonosítójú főkönyvi számla adatait."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres frissítés",
                    content = @Content(schema = @Schema(implementation = GLAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public GLAccountResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GLAccountUpdateRequest request
    ) {
        return service.update(id, request);
    }

    // ---------------------------------------------------------
    // DELETE (soft delete)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Főkönyvi számla törlése (logikai törlés)",
            description = "A számla active mezője false-ra áll, fizikai törlés nem történik."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ---------------------------------------------------------
    // RESTORE
    // ---------------------------------------------------------
    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Törölt főkönyvi számla visszaállítása",
            description = "A számla active mezője true-ra áll, így újra aktív lesz."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres visszaállítás",
                    content = @Content(schema = @Schema(implementation = GLAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public GLAccountResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
