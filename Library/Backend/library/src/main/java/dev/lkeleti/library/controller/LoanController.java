package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
import dev.lkeleti.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a kölcsönzéssel")
public class LoanController {
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új kölcsönzés rögzítése",
            description = "Új kölcsönzés rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új kölcsönzés létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CheckoutRequestDto.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "A kölcsönzés sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "409", description = "A könyv már ki van kölcsönözve")
    public LoanDto checkoutBook(@Valid @RequestBody CheckoutRequestDto checkoutRequestDto) {
        return loanService.checkoutBook(checkoutRequestDto);
    }

    @PutMapping("/book/{bookId}/return")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Kölcsönzés lezárása",
            description = "Kikölcsönzött könyv visszaérkeztetése az azonosító és a request body-ban megadott adatok alapján."
    )
    @ApiResponse(responseCode = "200", description = "A kölcsönzés sikeresen lezárva")
    @ApiResponse(responseCode = "404", description = "A könyv nem található")
    public LoanDto returnBook(@Parameter(description = "A visszaérkeztetett könyv egyedi azonosítója (ID)", required = true, example = "1") @PathVariable Long bookId) {
        return loanService.returnBook(bookId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes kölcsönzés listázása",
            description = "Visszaadja az összes eddigi kölcsönzés listáját (aktív és lezárt egyaránt).")
    @ApiResponse(responseCode = "200", description = "Kölcsönzések sikeresen listázva")
    public List<LoanDto> listLoans() {
        return loanService.listLoans();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes aktív kölcsönzés listázása",
            description = "Visszaadja az összes aktív kölcsönzés listáját.")
    @ApiResponse(responseCode = "200", description = "Aktív kölcsönzések sikeresen listázva")
    public List<LoanDto> listActiveLoans() {
        return loanService.listActiveLoans();
    }

    @GetMapping("/overdue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes lejárt kölcsönzés listázása",
            description = "Visszaadja az összes lejárt kölcsönzés listáját.")
    @ApiResponse(responseCode = "200", description = "Lejárt kölcsönzések sikeresen listázva")
    public List<LoanDto> listOverdueLoans() {
        return loanService.listOverdueLoans();
    }

    @GetMapping("/history/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy könyv kölcsönzési történetének lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú könyv kölcsönzési előzményeit.")
    @ApiResponse(responseCode = "200", description = "A könyv előzményeinek sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "A könyv nem található")
    public List<LoanDto> getLoanHistoryForBook(@Parameter(description = "Annak a könyvnek az ID-ja, amelynek a kölcsönzési előzményeit keressük", required = true, example = "1") @PathVariable Long bookId) {
        return loanService.getLoanHistoryForBook( bookId);
    }
}
