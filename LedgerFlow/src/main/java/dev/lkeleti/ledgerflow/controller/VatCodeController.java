package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.VatCodeCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.VatCodeUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.VatCodeResponse;
import dev.lkeleti.ledgerflow.service.VatCodeService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/vat-codes")
@RequiredArgsConstructor
@Tag(
        name = "VAT Code API",
        description = "ÁFA kódok kezelése, létrehozása, módosítása, logikai törlése és visszaállítása"
)
public class VatCodeController {

    private final VatCodeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új ÁFA kód létrehozása")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sikeres létrehozás",
                    content = @Content(schema = @Schema(implementation = VatCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "A kód már létezik",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VatCodeResponse create(@Valid @RequestBody VatCodeCreateRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "ÁFA kód lekérése ID alapján")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = VatCodeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VatCodeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív ÁFA kód lekérése")
    public List<VatCodeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes ÁFA kód lekérése (töröltekkel együtt)")
    public List<VatCodeResponse> getAllIncludingDeleted() {
        return service.getAllIncludingDeleted();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "ÁFA kód módosítása")
    public VatCodeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VatCodeUpdateRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "ÁFA kód logikai törlése")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Törölt ÁFA kód visszaállítása")
    public VatCodeResponse restore(@PathVariable Long id) {
        return service.restore(id);
    }
}
