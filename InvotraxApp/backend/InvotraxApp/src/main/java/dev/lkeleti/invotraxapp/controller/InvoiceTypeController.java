package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceTypeCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceTypeDto;
import dev.lkeleti.invotraxapp.dto.UpdateInvoiceTypeCommand;
import dev.lkeleti.invotraxapp.service.InvoiceTypeService;
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
@RequestMapping("/api/invoice_types")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a számla típusokkal")
public class InvoiceTypeController {
    private InvoiceTypeService invoiceTypeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes számla típus listázása",
            description = "Visszaadja az összes számla típus listáját még a törölteket is.")
    @ApiResponse(responseCode = "200", description = "Számla típus sikeresen listázva")
    public List<InvoiceTypeDto> getAllInvoiceTypes() {
        return invoiceTypeService.getAllInvoiceTypes();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív számla típus listázása",
            description = "Visszaadja az összes számla típus listáját, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "Számla típus sikeresen listázva")
    public List<InvoiceTypeDto> getAllActiveInvoiceTypes() {
        return invoiceTypeService.getAllActiveInvoiceTypes();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy számla típus lekérdezése azonosító alapján",
            description = "Visszaadja a megadott számla típus adatait.")
    @ApiResponse(responseCode = "200", description = "Számla típus sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Számla típus nem található")
    public InvoiceTypeDto getInvoiceTypeById(@PathVariable long id) {
        return invoiceTypeService.getInvoiceTypeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Számla típus adatainak módosítása",
            description = "Meglévő számla típus adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Számla típus adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateInvoiceTypeCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Számla típus sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó számla típus nem található")
    public InvoiceTypeDto updateInvoiceType(@PathVariable Long id, @RequestBody UpdateInvoiceTypeCommand command) {
        return invoiceTypeService.updateInvoiceType(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új számla típus rögzítése",
            description = "Új számla típus rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új számla típus létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInvoiceTypeCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Számla típus sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public InvoiceTypeDto createInvoiceType(@RequestBody CreateInvoiceTypeCommand command) {
        return invoiceTypeService.createInvoiceType(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Számla típus törlése",
            description = "Számla típus törlése a megadott azonosító alapján (logikai törlés)."
    )
    @ApiResponse(responseCode = "204", description = "Számla típus sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő számla típus nem található")
    public void deleteInvoiceType(@PathVariable Long id) {
        invoiceTypeService.deleteInvoiceType(id);
    }

    @DeleteMapping("/undelete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Számla típus törlésének megszüntetése",
            description = "Számla típus visszaállítása aktív állapotba a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "200", description = "Számla típus sikeresen visszaállítva")
    @ApiResponse(responseCode = "404", description = "Számla típus nem található")
    public InvoiceTypeDto unDeleteInvoiceType(@PathVariable Long id) {
        return invoiceTypeService.unDeleteInvoiceType(id);
    }
}




