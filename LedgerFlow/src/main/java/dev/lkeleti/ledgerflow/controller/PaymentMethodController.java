package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.PaymentMethodCreateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.PaymentMethodResponse;
import dev.lkeleti.ledgerflow.service.PaymentMethodService;
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
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Tag(
        name = "Payment Method API",
        description = "Fizetési módok kezelése, létrehozása, módosítása, logikai törlése és visszaállítása"
)
public class PaymentMethodController {

    private final PaymentMethodService service;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Új fizetési mód létrehozása",
            description = "Létrehoz egy új fizetési módot a megadott adatok alapján."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "A fizetési mód sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A fizetési mód kódja már létezik",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PaymentMethodResponse create(
            @Valid @RequestBody PaymentMethodCreateRequest request
    ) {
        return service.create(request);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Fizetési mód lekérése ID alapján",
            description = "Visszaadja a megadott azonosítójú aktív fizetési módot."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A fizetési mód nem található vagy törölt",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PaymentMethodResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // ---------------------------------------------------------
    // GET ALL (only active)
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes aktív fizetési mód lekérése",
            description = "Visszaadja az összes aktív (nem törölt) fizetési módot."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PaymentMethodResponse.class))
            )
    )
    public List<PaymentMethodResponse> getAll() {
        return service.getAll();
    }

    // ---------------------------------------------------------
    // GET ALL INCLUDING DELETED
    // ---------------------------------------------------------
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Összes fizetési mód lekérése (töröltekkel együtt)",
            description = "Visszaadja az összes fizetési módot, beleértve a logikailag törölteket is."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PaymentMethodResponse.class))
            )
    )
    public List<PaymentMethodResponse> getAllIncludingDeleted() {
        return service.getAllIncludingDeleted();
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Fizetési mód módosítása",
            description = "Frissíti a megadott azonosítójú fizetési mód adatait."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres frissítés",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A fizetési mód nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PaymentMethodResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodCreateRequest request
    ) {
        return service.update(id, request);
    }

    // ---------------------------------------------------------
    // DELETE (soft delete)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Fizetési mód logikai törlése",
            description = "A fizetési mód active mezője false-ra áll, fizikai törlés nem történik."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
            @ApiResponse(
                    responseCode = "404",
                    description = "A fizetési mód nem található",
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
            summary = "Törölt fizetési mód visszaállítása",
            description = "A fizetési mód active mezője true-ra áll, így újra aktív lesz."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres visszaállítás",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A fizetési mód nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public PaymentMethodResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
