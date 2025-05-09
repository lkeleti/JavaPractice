package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.service.InventoryService;
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
@RequestMapping("/api/inventories")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a bevételezéssel")
public class InventoryController {
    private InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes bevételezés listázása oldalakban (paging)",
            description = "Visszaadja az összes bevételezés listáját oldalakban, page/size paraméterezéssel és irányítószámra, vagy városra történő kereséssel/szűkítéssel.")
    @ApiResponse(responseCode = "200", description = "Bevételezések sikeresen listázva")
    public Page<InventoryDto> getAllInventories(Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return inventoryService.getAllInventories(pageable, searchTerm);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy bevételezés lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú bevételezés adatait.")
    @ApiResponse(responseCode = "200", description = "Bevételezés sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Bevételezés nem található")
    public InventoryDto getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új bevételezés rögzítése",
            description = "Új bevételezés rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új bevételezés létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInventoryCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Bevételezés sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "A szállító nem található")
    @ApiResponse(responseCode = "404", description = "A termék nem található")
    public InventoryDto createInventory(@RequestBody CreateInventoryCommand createInventoryCommand) {
        return inventoryService.createInventory(createInventoryCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Bevételezés adatainak módosítása",
            description = "Meglévő bevételezés adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bevételezés adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateInventoryCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Bevételezés sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó bevételezés nem található")
    @ApiResponse(responseCode = "404", description = "A szállító nem található")
    @ApiResponse(responseCode = "404", description = "A termék nem található")
        public InventoryDto updateInventory(@PathVariable Long id, @RequestBody UpdateInventoryCommand updateInventoryCommand) {
        return inventoryService.updateInventory(id, updateInventoryCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Bevételezés törlése",
            description = "Bevételezés törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Bevételezés sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő bevételezés nem található")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
