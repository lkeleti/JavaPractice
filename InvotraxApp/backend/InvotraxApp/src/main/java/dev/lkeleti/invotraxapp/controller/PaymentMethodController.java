package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.dto.PaymentMethodDto;
import dev.lkeleti.invotraxapp.dto.UpdatePaymentMethodCommand;
import dev.lkeleti.invotraxapp.dto.ZipCodeDto;
import dev.lkeleti.invotraxapp.service.PaymentMethodService;
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
@RequestMapping("/api/paymentmethods")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a fizetési móddal")
public class PaymentMethodController {
    private PaymentMethodService paymentMethodService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes fizetési mód listázása",
            description = "Visszaadja az összes fizetési mód listáját.")
    @ApiResponse(responseCode = "200", description = "Fizetési módok sikeresen listázva")
    public List<PaymentMethodDto> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív fizetési mód listázása",
            description = "Visszaadja az összes fizetési mód listáját, de csak az aktívakat.")
    @ApiResponse(responseCode = "200", description = "Fizetési mód sikeresen listázva")
    public List<PaymentMethodDto> getAllActivePaymentMethods() {
        return paymentMethodService.getAllActivePaymentMethods();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy fizetési mód lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú fizetési mód adatait.")
    @ApiResponse(responseCode = "200", description = "Fizetési mód sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Fizetési mód nem található")
    public PaymentMethodDto getPaymentMethodById(@PathVariable Long id) {
        return paymentMethodService.getPaymentMethodById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Fizetési mód módosítása",
            description = "Meglévő fizetési mód módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A fizetési mód  módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdatePaymentMethodCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Fizetési mód sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó fizetési mód nem található")
    public PaymentMethodDto updatePaymentMethod(@PathVariable Long id, @RequestBody UpdatePaymentMethodCommand command) {
        return paymentMethodService.updatePaymentMethod(id, command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új fizetési mód rögzítése",
            description = "Új fizetési mód rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az fizetési mód létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreatePaymentMethodCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Fizetési mód sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public PaymentMethodDto createPaymentMethod(@RequestBody CreatePaymentMethodCommand command) {
        return paymentMethodService.createPaymentMethod(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Fizetési mód törlése",
            description = "Fizetési mód törlése a megadott azonosító alapján (logikai törlés)."
    )
    @ApiResponse(responseCode = "204", description = "Fizetési mód sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő fizetési mód nem található")
    public void deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
    }

    @DeleteMapping("/undelete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Fizetési mód törlésének megszüntetése",
            description = "Fizetési mód visszaállítása aktív állapotba a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "200", description = "Fizetési mód sikeresen visszaállítva")
    @ApiResponse(responseCode = "404", description = "Fizetési mód nem található")
    public PaymentMethodDto unDeletePaymentMethod(@PathVariable Long id) {
        return paymentMethodService.unDeletePaymentMethod(id);
    }
}
