package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateProductCategoryCommand;
import dev.lkeleti.invotraxapp.dto.ProductCategoryDto;
import dev.lkeleti.invotraxapp.dto.UpdateProductCategoryCommand;
import dev.lkeleti.invotraxapp.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcategories")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class ProductCategoryController {
    private ProductCategoryService productCategoryService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes termék kategória listázása",
            description = "Visszaadja az összes regisztrált termék kategória listáját.")
    @ApiResponse(responseCode = "200", description = "termék kategóriák sikeresen listázva")
    public List<ProductCategoryDto> getAllProductCategories() {
        return productCategoryService.getAllProductCategories();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy termék kategória lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú termék kategória adatait.")
    @ApiResponse(responseCode = "200", description = "Termék kategória sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Termék kategória nem található")
    public ProductCategoryDto getProductCategoryById(@PathVariable Long id) {
        return productCategoryService.getProductCategoryById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Termék kategória adatainak módosítása",
            description = "Meglévő termék kategória adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A termék kategória adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductCategoryCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Termék kategória sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó termék kategória nem található")
    public ProductCategoryDto updateProductCategory(@PathVariable Long id, @RequestBody UpdateProductCategoryCommand command) {
        return productCategoryService.updateProductCategory(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új termék kategória rögzítése",
            description = "Új termék kategória rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az termék kategória létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductCategoryCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Termék kategória sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ProductCategoryDto createProductCategory(@RequestBody CreateProductCategoryCommand command) {
        return productCategoryService.createProductCategory(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Termék kategória törlése",
            description = "Termék kategória törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Termék kategória sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő termék kategória nem található")
    public void deleteProductCategory(@PathVariable Long id) {
        productCategoryService.deleteProductCategory(id);
    }
}
