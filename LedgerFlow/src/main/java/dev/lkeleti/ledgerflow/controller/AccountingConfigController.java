package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.AccountingConfigRequest;
import dev.lkeleti.ledgerflow.dto.response.AccountingConfigResponse;
import dev.lkeleti.ledgerflow.dto.response.ApiError;
import dev.lkeleti.ledgerflow.service.AccountingConfigService;
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

@RestController
@RequestMapping("/api/accounting-config")
@RequiredArgsConstructor
@Tag(
        name = "Accounting Config API",
        description = "Könyvelési beállítások lekérése, létrehozása és módosítása (egyetlen konfigurációs rekord)"
)
public class AccountingConfigController {

    private final AccountingConfigService service;

    // ---------------------------------------------------------
    // GET
    // ---------------------------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Könyvelési beállítások lekérése",
            description = "Visszaadja az aktuális könyvelési beállításokat. Ha nincs konfiguráció, hibát ad vissza."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sikeres lekérés",
                    content = @Content(schema = @Schema(implementation = AccountingConfigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A könyvelési beállítás nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AccountingConfigResponse get() {
        return service.get();
    }


    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Könyvelési beállítás létrehozása",
            description = "Létrehozza a könyvelési beállításokat a megadott adatok alapján. " +
                    "Ha már létezik konfiguráció, hibát ad vissza."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "A beállítás sikeresen létrehozva",
                    content = @Content(schema = @Schema(implementation = AccountingConfigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok a kérésben",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A megadott számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Már létezik könyvelési beállítás, új nem hozható létre",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AccountingConfigResponse create(
            @Valid @RequestBody AccountingConfigRequest request
    ) {
        return service.create(request);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Könyvelési beállítás módosítása",
            description = "Frissíti a meglévő könyvelési beállításokat a megadott adatok alapján. " +
                    "Ha nincs konfiguráció, hibát ad vissza."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "A beállítás sikeresen frissítve",
                    content = @Content(schema = @Schema(implementation = AccountingConfigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Érvénytelen adatok a kérésben",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "A könyvelési beállítás vagy a megadott számla nem található",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AccountingConfigResponse update(
            @Valid @RequestBody AccountingConfigRequest request
    ) {
        return service.update(request);
    }
}