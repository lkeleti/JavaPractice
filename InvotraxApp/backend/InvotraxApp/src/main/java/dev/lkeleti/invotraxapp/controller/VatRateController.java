package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.service.VatRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vatrates")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a ÁFA kódokkal")
public class VatRateController {
    private VatRateService vatRateService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes ÁFA listázása",
            description = "Visszaadja az összes ÁFA listáját még a törölteket is.")
    @ApiResponse(responseCode = "200", description = "ÁFA sikeresen listázva")
    public List<VatRateDto> getAllVatRates() {
        return vatRateService.getAllVatRates();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív ÁFA listázása",
            description = "Visszaadja az összes ÁFA listáját, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "ÁFA sikeresen listázva")
    public List<VatRateDto> getAllActiveVatRates() {
        return vatRateService.getAllActiveVatRates();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy ÁFA lekérdezése azonosító alapján",
            description = "Visszaadja a megadott ÁFA adatait.")
    @ApiResponse(responseCode = "200", description = "ÁFA sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "ÁFA nem található")
    public VatRateDto getVatRateById(@PathVariable long id) {
        return vatRateService.getVatRateById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "ÁFA adatainak módosítása",
            description = "Meglévő ÁFA adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ÁFA adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateVatRateCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "ÁFA sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó ÁFA nem található")
    public VatRateDto updateVatRate(@PathVariable Long id, @RequestBody UpdateVatRateCommand command) {
        return vatRateService.updateVatRate(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új ÁFA rögzítése",
            description = "Új ÁFA rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új ÁFA létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateVatRateCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "ÁFA sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public VatRateDto createVatRate(@RequestBody CreateVatRateCommand command) {
        return vatRateService.createVatRate(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "ÁFA törlése",
            description = "ÁFA törlése a megadott azonosító alapján (logikai törlés)."
    )
    @ApiResponse(responseCode = "204", description = "ÁFA sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő ÁFA nem található")
    public void deleteVatRate(@PathVariable Long id) {
        vatRateService.deleteVatRate(id);
    }

    @DeleteMapping("/undelete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "ÁFA törlésének megszüntetése",
            description = "ÁFA visszaállítása aktív állapotba a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "200", description = "ÁFA sikeresen visszaállítva")
    @ApiResponse(responseCode = "404", description = "ÁFA nem található")
    public VatRateDto unDeleteVatRate(@PathVariable Long id) {
        return vatRateService.unDeleteVatRate(id);
    }
}
