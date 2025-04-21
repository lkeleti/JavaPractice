package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.BarcodeDto;
import dev.lkeleti.invotraxapp.dto.CreateBarcodeCommand;
import dev.lkeleti.invotraxapp.service.BarcodeService;
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
@RequestMapping("/api/barcodes")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a vonlakóddal")
public class BarcodeController {
    private BarcodeService barcodeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes vonalkód listázása",
            description = "Visszaadja az összes vonalkód listáját.")
    @ApiResponse(responseCode = "200", description = "Vonalkódok sikeresen listázva")
    public List<BarcodeDto> getAllBarcodes() {
        return barcodeService.getAllBarcodes();
    }

    @GetMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy vonalkód lekérdezése vonalkód alapján",
            description = "Visszaadja a megadott vonalkódú objektum adatait.")
    @ApiResponse(responseCode = "200", description = "Vonalkód sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Vonalkód nem található")
    public BarcodeDto getBarcodeByCode(@PathVariable String code) {
        return barcodeService.getBarcodeByCode(code);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Vonalkód generálása",
            description = "Generál egy vonalkódot a prefix 9999."
    )
    @ApiResponse(responseCode = "200", description = "Vonalkód sikeresen generálva")
    public String generateBarcode() {
        return barcodeService.generateBarcode();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új vonalkód rögzítése",
            description = "Új vonalkód rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új vonalkód létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateBarcodeCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Vonalkód sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public BarcodeDto createBarcode(@RequestBody CreateBarcodeCommand command) {
        return barcodeService.createBarcode(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Vonalkód törlése",
            description = "Vonalkód törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Vonalkód sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő vonalkód nem található")
    public void deleteBarcode(@PathVariable Long id) {
        barcodeService.deleteBarcode(id);
    }
}
