package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateZipCodeCommand;
import dev.lkeleti.invotraxapp.dto.ManufacturerDto;
import dev.lkeleti.invotraxapp.dto.UpdateZipCodeCommand;
import dev.lkeleti.invotraxapp.dto.ZipCodeDto;
import dev.lkeleti.invotraxapp.service.ZipCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zipcodes")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a irányítószámmal")
public class ZipCodeController {
    private ZipCodeService zipCodeService;

/*    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes irányítószám listázása",
            description = "Visszaadja az összes irányítószám listáját még a törölteket is.")
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen listázva")
    public List<ZipCodeDto> getAllZipCodes() {
        return zipCodeService.getAllZipCodes();
    }*/

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes irányítószám listázása oldalakban (paging)",
            description = "Visszaadja az összes irányítószám listáját még a törölteket is oldalakban, page/size paraméterezéssel és irányítószámra, vagy városra történő kereséssel/szűkítéssel.")
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen listázva")
    public Page<ZipCodeDto> getAllZipCodes(Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return zipCodeService.getAllZipCodes(pageable, searchTerm);
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív irányítószám listázása",
            description = "Visszaadja az összes irányítószám listáját, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen listázva")
    public List<ZipCodeDto> getAllActiveZipCodes() {
        return zipCodeService.getAllActiveZipCodes();
    }

    @GetMapping("/active/{zip}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív irányítószám listázása irányítószám alapján",
            description = "Visszaadja az összes irányítószám listáját, ami megfelel a kritériumnak, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen listázva")
    public List<ZipCodeDto> getAllActiveZipCodesByZip(@PathVariable String zip) {
        return zipCodeService.getAllActiveZipCodesByZip(zip);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy irányítószám lekérdezése azonosító alapján",
            description = "Visszaadja a megadott irányítószám adatait.")
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Irányítószám nem található")
    public ZipCodeDto getZipCodeById(@PathVariable long id) {
        return zipCodeService.getZipCodeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Irányítószám adatainak módosítása",
            description = "Meglévő irányítószám adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "irányítószám adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateZipCodeCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó irányítószám nem található")
    public ZipCodeDto updateZipCode(@PathVariable Long id, @RequestBody UpdateZipCodeCommand command) {
        return zipCodeService.updateZipCode(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új irányítószám rögzítése",
            description = "Új irányítószám rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új irányítószám létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateZipCodeCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Irányítószám sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ZipCodeDto createZipCode(@RequestBody CreateZipCodeCommand command) {
        return zipCodeService.createZipCode(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Irányítószám törlése",
            description = "Irányítószám törlése a megadott azonosító alapján (logikai törlés)."
    )
    @ApiResponse(responseCode = "204", description = "Irányítószám sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő irányítószám nem található")
    public void deleteZipCode(@PathVariable Long id) {
        zipCodeService.deleteZipCode(id);
    }

    @DeleteMapping("/undelete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Irányítószám törlésének megszüntetése",
            description = "Irányítószám visszaállítása aktív állapotba a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "200", description = "Irányítószám sikeresen visszaállítva")
    @ApiResponse(responseCode = "404", description = "Irányítószám nem található")
    public ZipCodeDto unDeleteZipCode(@PathVariable Long id) {
        return zipCodeService.unDeleteZipCode(id);
    }
}
