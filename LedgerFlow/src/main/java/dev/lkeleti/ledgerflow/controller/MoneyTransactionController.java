package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.MoneyTransactionCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.MoneyTransactionResponse;
import dev.lkeleti.ledgerflow.service.MoneyTransactionService;
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
@RequestMapping("/api/money-transactions")
@RequiredArgsConstructor
@Tag(
        name = "Money Transaction API",
        description = "Pénzügyi tranzakciók kezelése, létrehozása, listázása, logikai törlése és visszaállítása"
)
public class MoneyTransactionController {

    private final MoneyTransactionService service;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Új pénzügyi tranzakció létrehozása",
            description = "Létrehoz egy új pénzügyi tranzakciót és automatikusan könyveli is."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "A tranzakció sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = MoneyTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok a kérésben",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A megadott pénzügyi számla vagy számla (invoice) nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public MoneyTransactionResponse create(
            @Valid @RequestBody MoneyTransactionCreateRequest request
    ) {
        return service.create(request);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Pénzügyi tranzakció lekérése ID alapján",
            description = "Visszaadja a megadott azonosítójú aktív pénzügyi tranzakciót."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = MoneyTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A tranzakció nem található vagy törölt",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public MoneyTransactionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // ---------------------------------------------------------
    // GET ALL (only active)
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes aktív pénzügyi tranzakció lekérése",
            description = "Visszaadja az összes aktív (nem törölt) pénzügyi tranzakciót."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = MoneyTransactionResponse.class))
            )
    )
    public List<MoneyTransactionResponse> getAll() {
        return service.getAll();
    }

    // ---------------------------------------------------------
    // GET ALL INCLUDING DELETED
    // ---------------------------------------------------------
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes pénzügyi tranzakció lekérése (töröltekkel együtt)",
            description = "Visszaadja az összes pénzügyi tranzakciót, beleértve a logikailag törölteket is."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = MoneyTransactionResponse.class))
            )
    )
    public List<MoneyTransactionResponse> getAllIncludingDeleted() {
        return service.getAllIncludingDeleted();
    }

    // ---------------------------------------------------------
    // DELETE (soft delete)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Pénzügyi tranzakció logikai törlése",
            description = "A tranzakció deleted mezője true-ra áll, fizikai törlés nem történik."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
            @ApiResponse(
                    responseCode = "404",
                    description = "A tranzakció nem található",
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
            summary = "Törölt pénzügyi tranzakció visszaállítása",
            description = "A tranzakció deleted mezője false-ra áll, így újra aktív lesz."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres visszaállítás",
                    content = @Content(schema = @Schema(implementation = MoneyTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A tranzakció nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public MoneyTransactionResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
