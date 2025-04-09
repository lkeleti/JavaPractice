package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.BookDto;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.repository.AuthorRepository;
import dev.lkeleti.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test AuthorController")
@Sql(statements = {"DELETE FROM loans;", "DELETE FROM books;", "DELETE FROM authors;"})
public class BookControllerIT {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    WebTestClient webTestClient;

    BookDto bookOneDto;
    BookDto bookTwoDto;

    AuthorDto authorOneDto;
    AuthorDto authorTwoDto;

    @BeforeEach
    void init() {

        Author authorOne = authorRepository.save(new Author("John Doe",1970,"Magyar"));
        Author authorTwo = authorRepository.save(new Author("Jack Doe",1980,"Német"));

        Book bookOne = bookRepository.save(new Book("1111","Game Of Trones", 2000, authorOne));
        Book bookTwo = bookRepository.save(new Book("2222","Harry Potter", 1998, authorTwo));

        authorOneDto = new AuthorDto(authorOne.getId(), authorOne.getName(), authorOne.getBirthYear(), authorOne.getNationality(), new ArrayList<>());
        authorTwoDto = new AuthorDto(authorTwo.getId(), authorTwo.getName(), authorTwo.getBirthYear(), authorTwo.getNationality(), new ArrayList<>());

        bookOneDto = new BookDto(bookOne.getId(), bookOne.getIsbn(), bookOne.getTitle(), bookOne.getPublicationYear(), authorOneDto);
        bookTwoDto = new BookDto(bookTwo.getId(), bookTwo.getIsbn(), bookTwo.getTitle(), bookTwo.getPublicationYear(), authorTwoDto);
    }

    @Test
    @DisplayName("List all books")
    void testListAllBooks() {
        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(2)
                .value(t -> assertThat(t).extracting(BookDto::getIsbn).containsExactly("1111", "2222"));
    }

    @Test
    @DisplayName("List book by ID")
    void testListBookById() {
        webTestClient.get()
                .uri("/api/books/{id}", bookOneDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(t -> {
                    assertThat(t.getId()).isEqualTo(bookOneDto.getId());
                    assertThat(t.getIsbn()).isEqualTo(bookOneDto.getIsbn());
                    assertThat(t.getTitle()).isEqualTo(bookOneDto.getTitle());
                    assertThat(t.getPublicationYear()).isEqualTo(bookOneDto.getPublicationYear());
                });
    }

    @Test
    @DisplayName("Testing get book by ID with invalid value")
    void testGetBookByInvalidId() {
        webTestClient.get()
                .uri("/api/books/{id}", -1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }
}
