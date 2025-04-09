package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.*;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.model.Book;
import dev.lkeleti.library.model.Loan;
import dev.lkeleti.library.repository.AuthorRepository;
import dev.lkeleti.library.repository.BookRepository;
import dev.lkeleti.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test BookController")
@Sql(statements = {"DELETE FROM loans;", "DELETE FROM books;", "DELETE FROM authors;"})
class BookControllerIT {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    LoanRepository loanRepository;

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

        Loan loan = loanRepository.save(new Loan("Joe Doe", LocalDate.now(), LocalDate.now().plusDays(14),bookTwo));
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

    @Test
    @DisplayName("Test create new book")
    void testCreateNewBook() {
        CreateBookCommand command = new CreateBookCommand("3333","The big bang theory",2005, authorOneDto.getId());

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class)
                .value(t-> {
                    assertThat(t.getIsbn()).isEqualTo(command.getIsbn());
                    assertThat(t.getTitle()).isEqualTo(command.getTitle());
                    assertThat(t.getPublicationYear()).isEqualTo(command.getPublicationYear());
                    assertThat(t.getId()).isNotNull();
                });

        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(3);
    }

    @Test
    @DisplayName("Test create new book with blank isbn")
    void testCreateNewBook_error() {
        CreateBookCommand command = new CreateBookCommand("","Wrong book",2025, authorOneDto.getId());

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Test create new book with invalid author")
    void testCreateNewBook_BadAuthor() {
        CreateBookCommand command = new CreateBookCommand("3333","Wrong book",2025, -1L);

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(command)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Test create new book with non unique isbn")
    void testCreateNewBook_IsbnError() {
        CreateBookCommand command = new CreateBookCommand("1111","Wrong book",2025, authorOneDto.getId());

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(command)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict");
    }

    @Test
    @DisplayName("Test modify book title")
    void testModifyBookTitle() {
        UpdateBookCommand updateBookCommand = new UpdateBookCommand("1111","New title",2000, authorOneDto.getId());

        BookDto bookDto =
                webTestClient.put()
                        .uri("/api/books/{id}", bookOneDto.getId())
                        .bodyValue(updateBookCommand)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(BookDto.class)
                        .returnResult().getResponseBody();
        assertThat(bookDto.getIsbn()).isEqualTo("1111");
        assertThat(bookDto.getTitle()).isEqualTo("New title");
        assertThat(bookDto.getPublicationYear()).isEqualTo(2000);
    }

    @Test
    @DisplayName("Test modify book with blank isbn")
    void testModifyBookInvalidIsbn() {
        UpdateBookCommand updateBookCommand = new UpdateBookCommand("","Wrong book",2000, authorOneDto.getId());

        webTestClient.put()
                .uri("/api/books/{id}", bookOneDto.getId())
                .bodyValue(updateBookCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Test modify not exists book")
    void testModifyBook_NotFound() {
        UpdateBookCommand updateBookCommand = new UpdateBookCommand("1111","Wrong book",2000, authorOneDto.getId());

        webTestClient.put()
                .uri("/api/books/{id}", -1L)
                .bodyValue(updateBookCommand)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Test modify book with invalid author")
    void testModifyBook_BadAuthor() {
        UpdateBookCommand updateBookCommand = new UpdateBookCommand("1111","Wrong book",2000, -1L);

        webTestClient.put()
                .uri("/api/books/{id}", bookOneDto.getId())
                .bodyValue(updateBookCommand)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Test delete book")
    void testDeleteBook() {
        webTestClient.delete()
                .uri("/api/books/{id}", bookOneDto.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("Test delete not exists book")
    void testDeleteBookInvalidId() {
        webTestClient.delete()
                .uri("/api/books/{id}", -1L)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Test delete book but exists loan")
    void testDeleteBookAlreadyLoan() {
        webTestClient.delete()
                .uri("/api/books/{id}", bookTwoDto.getId())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict");
    }
}
