package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.FinancialAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.FinancialAccountResponse;
import dev.lkeleti.ledgerflow.service.FinancialAccountService;
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
@RequestMapping("/api/financial-accounts")
@RequiredArgsConstructor
@Tag(
        name = "Financial Account API",
        description = "Pénzügyi számlák kezelése, létrehozása, módosítása, logikai törlése és visszaállítása"
)
public class FinancialAccountController {

    private final FinancialAccountService service;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Új pénzügyi számla létrehozása",
            description = "Létrehoz egy új pénzügyi számlát a megadott adatok alapján."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "A számla sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = FinancialAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A megadott főkönyvi számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public FinancialAccountResponse create(
            @Valid @RequestBody FinancialAccountCreateRequest request
    ) {
        return service.create(request);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Pénzügyi számla lekérése ID alapján",
            description = "Visszaadja a megadott azonosítójú aktív pénzügyi számlát."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = FinancialAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található vagy törölt",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public FinancialAccountResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // ---------------------------------------------------------
    // GET ALL (only active)
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes aktív pénzügyi számla lekérése",
            description = "Visszaadja az összes aktív (nem törölt) pénzügyi számlát."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = FinancialAccountResponse.class))
            )
    )
    public List<FinancialAccountResponse> getAll() {
        return service.getAll();
    }

    // ---------------------------------------------------------
    // GET ALL INCLUDING DELETED
    // ---------------------------------------------------------
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes pénzügyi számla lekérése (töröltekkel együtt)",
            description = "Visszaadja az összes pénzügyi számlát, beleértve a logikailag törölteket is."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = FinancialAccountResponse.class))
            )
    )
    public List<FinancialAccountResponse> getAllIncludingDeleted() {
        return service.getAllIncludingDeleted();
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Pénzügyi számla módosítása",
            description = "Frissíti a megadott azonosítójú pénzügyi számla adatait."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres frissítés",
                    content = @Content(schema = @Schema(implementation = FinancialAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található vagy törölt",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public FinancialAccountResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FinancialAccountCreateRequest request
    ) {
        return service.update(id, request);
    }

    // ---------------------------------------------------------
    // DELETE (soft delete)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Pénzügyi számla törlése (logikai törlés)",
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
            summary = "Törölt pénzügyi számla visszaállítása",
            description = "A számla active mezője true-ra áll, így újra aktív lesz."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres visszaállítás",
                    content = @Content(schema = @Schema(implementation = FinancialAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public FinancialAccountResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
