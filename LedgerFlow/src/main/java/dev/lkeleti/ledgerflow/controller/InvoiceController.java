package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.*;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.InvoiceResponse;
import dev.lkeleti.ledgerflow.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Számlák", description = "Számlák rögzítése és módosítása")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ============================
    // GET BY ID
    // ============================

    @Operation(
            summary = "Számla lekérdezése ID alapján",
            description = "Visszaadja a számla teljes adatait, ÁFA bontással."
    )
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Sikeres lekérdezés",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "404", description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    // ============================
    // LIST + FILTER + PAGING
    // ============================

    @Operation(
            summary = "Számlák listázása",
            description = "Szűrés, rendezés és lapozás támogatott. Alapértelmezett rendezés: issueDate ASC."
    )
    @ApiResponse(responseCode = "200", description = "Sikeres lekérdezés",
            content = @Content(schema = @Schema(implementation = InvoiceResponse.class)))
    @GetMapping
    public Page<InvoiceResponse> list(
            InvoiceFilterRequest filter,
            @PageableDefault(sort = "issueDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return invoiceService.list(filter, pageable);
    }

    @Operation(
            summary = "Új számla rögzítése",
            description = "Új számla létrehozása, ÁFA bontással és automatikus könyveléssel."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A számla sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Partner vagy ÁFA kód nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse create(
            @Valid @RequestBody InvoiceCreateRequest request
    ) {
        return invoiceService.create(request);
    }

    @Operation(
            summary = "Számla módosítása",
            description = "Meglévő számla adatainak módosítása, ÁFA bontással és újrakönyveléssel."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A számla sikeresen módosítva",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public InvoiceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceUpdateRequest request
    ) {
        request.setId(id);
        return invoiceService.update(request);
    }

    @PostMapping("/{id}/storno")
    @Operation(
            summary = "Sztornó számla létrehozása",
            description = "Létrehoz egy sztornó számlát az eredeti számla alapján.",
            requestBody = @RequestBody(
                    description = "A sztornó számla létrehozásához szükséges adatok.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvoiceStornoRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A sztornó számla sikeresen létrehozva"),
            @ApiResponse(responseCode = "400", description = "A számla nem sztornózható",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createStorno(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceStornoRequest request
    ) {
        return invoiceService.createStorno(id, request);
    }

    @PostMapping("/{id}/correction")
    @Operation(
            summary = "Helyesbítő számla létrehozása",
            description = "Különbözet alapú helyesbítő számla létrehozása az eredeti számla módosításához."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A helyesbítő számla sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "400", description = "A számla nem helyesbíthető vagy lezárt időszak",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "A számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createCorrection(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceCorrectionRequest request
    ) {
        return invoiceService.createCorrection(id, request);
    }
}
