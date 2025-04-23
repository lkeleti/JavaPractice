package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.service.SerialNumberService;
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
@RequestMapping("/api/serialnumbers")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a gyáriszámokkal kódokkal")
public class SerialNumberController {
    private SerialNumberService serialNumberService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes gyáriszám listázása",
            description = "Visszaadja az összes gyáriszám listáját.")
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen listázva")
    public List<SerialNumberDto> getAllSerialNumbers() {
        return serialNumberService.getAllSerialNumbers();
    }

    @GetMapping("/used")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes használt gyáriszám listázása",
            description = "Visszaadja az összes használt gyáriszám listáját.")
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen listázva")
    public List<SerialNumberDto> getAllUsedSerialNumbers() {
        return serialNumberService.getAllUsedSerialNumbers();
    }

    @GetMapping("/not-used")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes nem használt gyáriszám listázása",
            description = "Visszaadja az összes nem használt gyáriszám listáját.")
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen listázva")
    public List<SerialNumberDto> getAllNotUsedSerialNumbers() {
        return serialNumberService.getAllNotUsedSerialNumbers();
    }

    @GetMapping("/details/{serialnumber}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Eladás részletes adatainak lekérése gyáriszám alapján",
            description = "Visszaadja az eladás részletes adatait gyáriszám alapján.")
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen listázva")
    public SerialNumberDetailsDto getDetailsBySerial(@PathVariable String serialnumber) {
        return serialNumberService.getDetailsBySerial(serialnumber);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy gyáriszám lekérdezése azonosító alapján",
            description = "Visszaadja a megadott gyáriszám adatait.")
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Gyáriszám nem található")
    public SerialNumberDto getSerialNumberById(@PathVariable long id) {
        return serialNumberService.getSerialNumberById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Gyáriszám adatainak módosítása",
            description = "Meglévő gyáriszám adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Gyáriszám adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateSerialNumberCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Gyáriszám sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó gyáriszám nem található")
    public SerialNumberDto updateSerialNumber(@PathVariable Long id, @RequestBody UpdateSerialNumberCommand command) {
        return serialNumberService.updateSerialNumber(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új gyáriszám rögzítése",
            description = "Új gyáriszám rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új gyáriszám létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateSerialNumberCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Gyáriszám sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public SerialNumberDto createSerialNumber(@RequestBody CreateSerialNumberCommand command) {
        return serialNumberService.createSerialNumber(command);
    }
}
