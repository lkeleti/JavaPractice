package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.*;
import dev.lkeleti.library.service.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class BookController {
    private BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookDto> listAllBook() {
        return bookService.listBooks();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto findBookById(@PathVariable long id) {
        return bookService.findBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody CreateBookCommand createBookCommand) {
        return bookService.createBook(createBookCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto updateBook(@PathVariable Long id, @Valid @RequestBody UpdateBookCommand updateBookCommand) {
        return bookService.updateBook(id, updateBookCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
