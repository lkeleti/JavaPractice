package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceNumberSequenceCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceNumberSequenceDto;
import dev.lkeleti.invotraxapp.dto.UpdateInvoiceNumberSequenceCommand;
import dev.lkeleti.invotraxapp.service.InvoiceNumberSequenceService;
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
@RequestMapping("/api/invoice-number-sequences")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a számlaszám prefixekkel")
public class InvoiceNumberSequenceController {
    private InvoiceNumberSequenceService invoiceNumberSequenceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes számlaszám prefix listázása",
            description = "Visszaadja az összes számlaszám prefix listáját.")
    @ApiResponse(responseCode = "200", description = "számlaszám prefix sikeresen listázva")
    public List<InvoiceNumberSequenceDto> getAllInvoiceNumberSequences() {
        return invoiceNumberSequenceService.getAllInvoiceNumberSequences();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy számlaszám prefix lekérdezése azonosító alapján",
            description = "Visszaadja a megadott számlaszám prefix adatait.")
    @ApiResponse(responseCode = "200", description = "Számlaszám prefix sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Számlaszám prefix nem található")
    public InvoiceNumberSequenceDto getInvoiceNumberSequenceById(@PathVariable long id) {
        return invoiceNumberSequenceService.getInvoiceNumberSequenceById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Számlaszám prefix adatainak módosítása",
            description = "Meglévő számlaszám prefix adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Számlaszám prefix adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateInvoiceNumberSequenceCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Számlaszám prefix sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó számlaszám prefix nem található")
    public InvoiceNumberSequenceDto updateInvoiceNumberSequence(@PathVariable Long id, @RequestBody UpdateInvoiceNumberSequenceCommand command) {
        return invoiceNumberSequenceService.updateInvoiceNumberSequence(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új számlaszám prefix rögzítése",
            description = "Új számlaszám prefix rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új számlaszám prefix létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateInvoiceNumberSequenceCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Számlaszám prefix sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public InvoiceNumberSequenceDto createInvoiceNumberSequence(@RequestBody CreateInvoiceNumberSequenceCommand command) {
        return invoiceNumberSequenceService.createInvoiceNumberSequence(command);
    }
}
