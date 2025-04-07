package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.*;
import dev.lkeleti.library.service.BookService;
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
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a könyvekkel")
public class BookController {
    private BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes könyv listázása",
            description = "Visszaadja az összes regisztrált könyv listáját.")
    @ApiResponse(responseCode = "200", description = "Könyvek sikeresen listázva")
    public List<BookDto> listAllBooks() {
        return bookService.listBooks();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy könyv lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú könyv adatait.")
    @ApiResponse(responseCode = "200", description = "A könyv sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "A könyv nem található")
    public BookDto findBookById(@Parameter(description = "A keresett könyv egyedi azonosítója (ID)", required = true, example = "1") @PathVariable long id) {
        return bookService.findBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új könyv rögzítése",
            description = "Új könyv rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új könyv létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateBookCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "A könyv sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public BookDto createBook(@Valid @RequestBody CreateBookCommand createBookCommand) {
        return bookService.createBook(createBookCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Könyv adatainak módosítása",
            description = "Meglévő könyv adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A könyv adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateBookCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "A könyv sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó könyv nem található")
    public BookDto updateBook(@Parameter(description = "A módosítandó könyv egyedi azonosítója (ID)", required = true, example = "1") @PathVariable Long id, @Valid @RequestBody UpdateBookCommand updateBookCommand) {
        return bookService.updateBook(id, updateBookCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Könyv törlése",
            description = "Könyv törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "A könyv sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő könyv nem található")
    public void deleteBook(@Parameter(description = "A törlendő könyv egyedi azonosítója (ID)", required = true, example = "1") @PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
