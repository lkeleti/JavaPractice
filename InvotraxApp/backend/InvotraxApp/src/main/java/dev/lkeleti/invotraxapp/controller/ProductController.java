package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.service.ProductService;
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
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a termékkel")
public class ProductController {
    private ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes termék listázása oldalakban (paging)",
            description = "Visszaadja az összes termék listáját még a törölteket is oldalakban, page/size paraméterezéssel és megnevezésre, vagy ena kódra, vagy gyártói kódra történő kereséssel/szűkítéssel.")
    @ApiResponse(responseCode = "200", description = "Termék sikeresen listázva")
    public Page<ProductDto> getAllProducts(Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return productService.getAllProducts(pageable, searchTerm);
    }
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív termék listázása",
            description = "Visszaadja az összes termék listáját, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "Termékek sikeresen listázva")
    public List<ProductDto> getAllActiveProducts() {
        return productService.getAllActiveProducts();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy termék lekérdezése azonosító alapján",
            description = "Visszaadja a megadott termék adatait.")
    @ApiResponse(responseCode = "200", description = "Termék sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Termék nem található")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új termék rögzítése",
            description = "Új termék rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új termék létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Termék sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public ProductDto createProduct(@RequestBody CreateProductCommand command) {
        return productService.createProduct(command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Termék adatainak módosítása",
            description = "Meglévő termék adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A termék adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Termék sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó termék nem található")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody UpdateProductCommand command) {
        return productService.updateProduct(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Termék törlése",
            description = "Termék törlése a megadott azonosító alapján (logikai törlés)."
    )
    @ApiResponse(responseCode = "204", description = "Termék sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő termék nem található")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @DeleteMapping("/undelete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Termék törlésének megszüntetése",
            description = "Termék visszaállítása aktív állapotba a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "200", description = "Termék sikeresen visszaállítva")
    @ApiResponse(responseCode = "404", description = "Termék nem található")
    public void unDeleteProduct(@PathVariable Long id) {
        productService.unDeleteProduct(id);
    }
}

