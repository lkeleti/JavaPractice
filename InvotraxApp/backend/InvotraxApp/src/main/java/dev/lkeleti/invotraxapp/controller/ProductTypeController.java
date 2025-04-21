package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateProductTypeCommand;
import dev.lkeleti.invotraxapp.dto.ProductTypeDto;
import dev.lkeleti.invotraxapp.dto.UpdateProductTypeCommand;
import dev.lkeleti.invotraxapp.service.ProductTypeService;
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
@RequestMapping("/api/product-types")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a termék típusokkal")
public class ProductTypeController {
    private ProductTypeService productTypeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes termék típus listázása",
            description = "Visszaadja az összes termék típus listáját még a törölteket is.")
    @ApiResponse(responseCode = "200", description = "Termék típus sikeresen listázva")
    public List<ProductTypeDto> getAllProductTypes() {
        return productTypeService.getAllProductTypes();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy termék típus lekérdezése azonosító alapján",
            description = "Visszaadja a megadott termék típus adatait.")
    @ApiResponse(responseCode = "200", description = "Termék típus sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Termék típus nem található")
    public ProductTypeDto getProductTypeById(@PathVariable long id) {
        return productTypeService.getProductTypeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Termék típus adatainak módosítása",
            description = "Meglévő termék típus adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Termék típus adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductTypeCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Termék típus sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó termék típus nem található")
    public ProductTypeDto updateProductType(@PathVariable Long id, @RequestBody UpdateProductTypeCommand command) {
        return productTypeService.updateProductType(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új termék típus rögzítése",
            description = "Új termék típus rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új termék típus létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductTypeCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Termék típus sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ProductTypeDto createProductType(@RequestBody CreateProductTypeCommand command) {
        return productTypeService.createProductType(command);
    }
}
