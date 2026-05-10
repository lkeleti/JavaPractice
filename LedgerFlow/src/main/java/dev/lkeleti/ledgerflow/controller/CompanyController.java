package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.AccountingPeriodChangeRequest;
import dev.lkeleti.ledgerflow.dto.request.CompanyCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.CompanyUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.dto.response.CompanyResponse;
import dev.lkeleti.ledgerflow.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@Tag(name = "Cég adatok", description = "A rendszerben tárolt saját cég adatainak kezelése")
public class CompanyController {

    private final CompanyService companyService;

    // ============================
    // CREATE
    // ============================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cég létrehozása",
            description = "Csak egy cég rögzíthető a rendszerben.",
            requestBody = @RequestBody(
                    description = "A cég létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyCreateRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "A cég sikeresen létrehozva"),
            @ApiResponse(responseCode = "400", description = "Már létezik cég",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public CompanyResponse create(
            @Valid @RequestBody CompanyCreateRequest request
    ) {
        return companyService.create(request);
    }

    // ============================
    // GET
    // ============================

    @Operation(summary = "Cég adatainak lekérdezése")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres lekérdezés"),
            @ApiResponse(responseCode = "404", description = "A cég nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public CompanyResponse get() {
        return companyService.get();
    }

    // ============================
    // UPDATE
    // ============================

    @PutMapping("/{id}")
    @Operation(
            summary = "Cég adatainak módosítása",
            description = "A cég törzsadatainak frissítése.",
            requestBody = @RequestBody(
                    description = "A módosítandó cég adatai JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyUpdateRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A cég sikeresen módosítva"),
            @ApiResponse(responseCode = "404", description = "A cég nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public CompanyResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CompanyUpdateRequest request
    ) {
        return companyService.update(id, request);
    }

    // ============================
    // CLOSE PERIOD
    // ============================

    @PostMapping("/close-period")
    @Operation(
            summary = "Könyvelési időszak zárása",
            description = "A closedAccountingPeriod mezőt előre állítja. Korábbi dátumra nem engedi visszaállítani.",
            requestBody = @RequestBody(
                    description = "A lezárni kívánt könyvelési időszak vége.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountingPeriodChangeRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres zárás"),
            @ApiResponse(responseCode = "400", description = "A könyvelési időszak nem állítható korábbi dátumra",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public CompanyResponse closePeriod(
            @Valid @RequestBody AccountingPeriodChangeRequest request
    ) {
        return companyService.closePeriod(request.getClosedAccountingPeriod());
    }

    // ============================
    // REOPEN PERIOD
    // ============================

    @PostMapping("/reopen-period")
    @Operation(
            summary = "Könyvelési időszak újranyitása",
            description = "A closedAccountingPeriod mezőt visszaállítja korábbi dátumra. Előre nem tolható.",
            requestBody = @RequestBody(
                    description = "Az újranyitott könyvelési időszak vége.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountingPeriodChangeRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sikeres újranyitás"),
            @ApiResponse(responseCode = "400", description = "Újranyitáskor a könyvelési időszak nem tolható előre",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public CompanyResponse reopenPeriod(
            @Valid @RequestBody AccountingPeriodChangeRequest request
    ) {
        return companyService.reopenPeriod(request.getClosedAccountingPeriod());
    }
}
