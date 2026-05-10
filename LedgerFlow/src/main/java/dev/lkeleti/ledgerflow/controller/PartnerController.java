package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.PartnerCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerFilterRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.PartnerResponse;
import dev.lkeleti.ledgerflow.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/partners")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequiredArgsConstructor
@Tag(name = "Partner API", description = "Partnerek kezelése, létrehozása, módosítása, törlése és visszaállítása")
public class PartnerController {

    private final PartnerService partnerService;
    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Partnerek listázása",
            description = "Lapozható, rendezhető és szűrhető partnerlista. A szűrési mezők: name, taxNumber, deleted, privatePerson."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sikeres lekérés – lapozott PartnerResponse lista",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
            )
    )
    public Page<PartnerResponse> list(
            @ParameterObject PartnerFilterRequest filter,
            @ParameterObject Pageable pageable
    ) {
        return partnerService.list(filter, pageable);
    }


    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Partner lekérése ID alapján",
            description = "Visszaadja a megadott azonosítójú partnert, ha létezik."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partner sikeresen lekérdezve"),
            @ApiResponse(responseCode = "404", description = "Partner not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public PartnerResponse getById(@PathVariable Long id) {
        return partnerService.getById(id);
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Új partner létrehozása",
            description = "Létrehoz egy új partnert a megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új partner létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PartnerCreateRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A partner sikeresen létrehozva"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Partner not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Customer account not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Supplier account not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    public PartnerResponse create(
            @Valid @RequestBody PartnerCreateRequest request
    ) {
        return partnerService.create(request);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Partner adatainak módosítása",
            description = "Frissíti a partner adatait a megadott ID alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A partner módosításához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PartnerUpdateRequest.class))
            )
    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A partner sikeresen frissítve"),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Partner not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Customer account not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Supplier account not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public PartnerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PartnerUpdateRequest request
    ) {
        return partnerService.update(id, request);
    }

    // ---------------------------------------------------------
    // DELETE (SOFT DELETE)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Partner törlése (logikai törlés)",
            description = "A partner deleted mezője true-ra áll, fizikai törlés nem történik."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "A partner sikeresen törölve"),
            @ApiResponse(responseCode = "404", description = "Partner not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })

    public void delete(@PathVariable Long id) {
        partnerService.delete(id);
    }

    // ---------------------------------------------------------
    // RESTORE
    // ---------------------------------------------------------
    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Törölt partner visszaállítása",
            description = "A partner deleted mezője false-ra áll, így újra aktív lesz."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A partner sikeresen visszaállítva"),
            @ApiResponse(responseCode = "404", description = "A visszaállítandó partner nem található", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public PartnerResponse restore(@PathVariable Long id) {
        return partnerService.restore(id);
    }
}
