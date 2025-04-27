package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateManufacturerCommand;
import dev.lkeleti.invotraxapp.dto.ManufacturerDto;
import dev.lkeleti.invotraxapp.dto.UpdateManufacturerCommand;
import dev.lkeleti.invotraxapp.service.ManufacturerService;
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


@RestController
@RequestMapping("/api/manufacturers")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a gyártóval")
public class ManufacturerController {
    private ManufacturerService manufacturerService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Gyártók listázása oldalakban (paging)",
            description = "Visszaadja az összes gyártót oldalakban, page/size paraméterezéssel.")
    @ApiResponse(responseCode = "200", description = "Gyártók sikeresen listázva")
    public Page<ManufacturerDto> getAllManufacturers(Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return manufacturerService.getAllManufacturers(pageable, searchTerm);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy gyártó lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú gyártó adatait.")
    @ApiResponse(responseCode = "200", description = "Gyártó sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Gyártó nem található")
    public ManufacturerDto getManufacturerById(@PathVariable Long id) {
        return manufacturerService.getManufacturerById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Gyártó adatainak módosítása",
            description = "Meglévő gyártó adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A gyártó adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateManufacturerCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Gyártó sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó gyártó nem található")
    public ManufacturerDto updateManufacturer(@PathVariable Long id, @RequestBody UpdateManufacturerCommand command) {
        return manufacturerService.updateManufacturer(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új gyártó rögzítése",
            description = "Új gyártó rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az gyártó létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateManufacturerCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Gyártó sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ManufacturerDto createManufacturer(@RequestBody CreateManufacturerCommand command) {
        return manufacturerService.createManufacturer(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Gyártó törlése",
            description = "Gyártó törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Gyártó sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő gyártó nem található")
    public void deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
    }
}
