package dev.lkeleti.library.controller;


import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.CreateAuthorCommand;
import dev.lkeleti.library.dto.UpdateAuthorCommand;
import dev.lkeleti.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class AuthorController {
    private AuthorService authorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorDto> listAllAuthor() {
        return authorService.listAuthors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto findAuthorById(@PathVariable long id) {
        return authorService.findAuthorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@Valid @RequestBody CreateAuthorCommand createAuthorCommand) {
        return authorService.createAuthor(createAuthorCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDto updateAuthor(@PathVariable Long id, @Valid @RequestBody UpdateAuthorCommand updateAuthorCommand) {
        return authorService.updateAuthor(id, updateAuthorCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }
}
