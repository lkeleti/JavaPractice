package dev.lkeleti.library.controller;


import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.CreateAuthorCommand;
import dev.lkeleti.library.dto.UpdateAuthorCommand;
import dev.lkeleti.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/authors")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a szerzővel")
public class AuthorController {
    private AuthorService authorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Összes szerző listázása",
            description = "Visszaadja az összes regisztrált szerző listáját.")
    @ApiResponse(responseCode = "200", description = "Szerzők sikeresen listázva")
    public List<AuthorDto> listAllAuthors() {
        return authorService.listAuthors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Egy szerző lekérdezése azonosító alapján",
            description = "Visszaadja a megadott azonosítójú szerző adatait.")
    @ApiResponse(responseCode = "200", description = "Szerző sikeresen lekérdezve")
    @ApiResponse(responseCode = "404", description = "Szerző nem található")
    public AuthorDto findAuthorById(@PathVariable long id) {
        return authorService.findAuthorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Új szerző rögzítése",
            description = "Új szerző rögzítése a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Az új szerző létrehozásához szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateAuthorCommand.class))
            )
    )
    @ApiResponse(responseCode = "201", description = "Szerző sikeresen létrehozva")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    public AuthorDto createAuthor(@Valid @RequestBody CreateAuthorCommand createAuthorCommand) {
        return authorService.createAuthor(createAuthorCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Szerző adatainak módosítása",
            description = "Meglévő szerző adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A szerző adatainak módosításához a szükséges adatok JSON formátumban.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateAuthorCommand.class))
            )
    )
    @ApiResponse(responseCode = "200", description = "Szerző sikeresen frissítve")
    @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
    @ApiResponse(responseCode = "404", description = "Módosítandó szerző nem található")
    public AuthorDto updateAuthor(@PathVariable Long id, @Valid @RequestBody UpdateAuthorCommand updateAuthorCommand) {
        return authorService.updateAuthor(id, updateAuthorCommand);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Szerző törlése",
            description = "Szerző törlése a megadott azonosító alapján."
    )
    @ApiResponse(responseCode = "204", description = "Szerző sikeresen törölve")
    @ApiResponse(responseCode = "404", description = "Törlendő szerző nem található")
    public void deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }
}
