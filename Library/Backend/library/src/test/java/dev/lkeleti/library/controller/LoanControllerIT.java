package dev.lkeleti.library.controller;


import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.BookDto;
import dev.lkeleti.library.dto.CheckoutRequestDto;
import dev.lkeleti.library.dto.LoanDto;
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
@DisplayName("Test LoanController")
@Sql(statements = {"DELETE FROM loans;", "DELETE FROM books;", "DELETE FROM authors;"})
class LoanControllerIT {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    WebTestClient webTestClient;

    Author authorOne;

    BookDto bookOneDto;
    BookDto bookTwoDto;
    BookDto bookThreeDto;

    AuthorDto authorOneDto;
    AuthorDto authorTwoDto;

    Loan loanTwo;
    Loan loanThree;

    @BeforeEach
    void init() {

        authorOne = authorRepository.save(new Author("John Doe",1970,"Magyar"));
        Author authorTwo = authorRepository.save(new Author("Jack Doe",1980,"Német"));

        Book bookOne = bookRepository.save(new Book("1111","Game Of Trones", 2000, authorOne));
        Book bookTwo = bookRepository.save(new Book("2222","Harry Potter", 1998, authorTwo));
        Book bookThree = bookRepository.save(new Book("3333","Lord of the rings", 1990, authorTwo));

        authorOneDto = new AuthorDto(authorOne.getId(), authorOne.getName(), authorOne.getBirthYear(), authorOne.getNationality(), new ArrayList<>());
        authorTwoDto = new AuthorDto(authorTwo.getId(), authorTwo.getName(), authorTwo.getBirthYear(), authorTwo.getNationality(), new ArrayList<>());

        bookOneDto = new BookDto(bookOne.getId(), bookOne.getIsbn(), bookOne.getTitle(), bookOne.getPublicationYear(), authorOneDto);
        bookTwoDto = new BookDto(bookTwo.getId(), bookTwo.getIsbn(), bookTwo.getTitle(), bookTwo.getPublicationYear(), authorTwoDto);
        bookThreeDto = new BookDto(bookThree.getId(), bookThree.getIsbn(), bookThree.getTitle(), bookThree.getPublicationYear(), authorTwoDto);

        Loan loanEntity = loanRepository.save(new Loan("Borrower One", LocalDate.of(2024,1,1), LocalDate.of(2024,1,1).plusDays(14),bookOne));
        loanEntity.setReturnDate(LocalDate.of(2024,1,14));
        loanRepository.saveAndFlush(loanEntity);

        loanTwo = loanRepository.save(new Loan("Borrower Two", LocalDate.now().minusDays(15), LocalDate.now().minusDays(1),bookOne));
        loanThree = loanRepository.save(new Loan("Borrower Three", LocalDate.now(), LocalDate.now().plusDays(14),bookTwo));
    }

    @Test
    @DisplayName("List all loans")
    void testListLoans_Multiple() {
        webTestClient.get()
                .uri("/api/loans")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(3)
                .value(t -> assertThat(t).extracting(LoanDto::getBorrowerName).containsExactly("Borrower One", "Borrower Two", "Borrower Three"));
    }

    @Test
    @DisplayName("List all loans but empty list found")
    void testListLoans_Empty() {
        loanRepository.deleteAll();
        webTestClient.get()
                .uri("/api/loans")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("List all active loans")
    void testListActiveLoans_Multiple() {
        webTestClient.get()
                .uri("/api/loans/active")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(2)
                .value(t -> assertThat(t).extracting(LoanDto::getBorrowerName).containsExactly("Borrower Two", "Borrower Three"));
    }

    @Test
    @DisplayName("List all active loans but empty list found")
    void testListActiveLoans_Empty() {
        Loan loanEntityTwo = loanRepository.findById(loanTwo.getId()).get();
        loanEntityTwo.setReturnDate(LocalDate.now());
        loanRepository.saveAndFlush(loanEntityTwo);

        Loan loanEntityThree = loanRepository.findById(loanThree.getId()).get();
        loanEntityThree.setReturnDate(LocalDate.now());
        loanRepository.saveAndFlush(loanEntityThree);

        webTestClient.get()
                .uri("/api/loans/active")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("List all overdue loans")
    void testListOverdueLoans_Multiple() {
        webTestClient.get()
                .uri("/api/loans/overdue")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(1)
                .value(t -> assertThat(t).extracting(LoanDto::getBorrowerName).containsExactly("Borrower Two"));
    }

    @Test
    @DisplayName("List all overdue loans but empty list found")
    void testListOverdueLoans_Empty() {
        loanRepository.deleteById(loanTwo.getId());

        webTestClient.get()
                .uri("/api/loans/overdue")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("List loan history by book")
    void testGetLoanHistoryForBook_Multiple() {
        webTestClient.get()
                .uri("/api/loans/history/{id}", bookOneDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(2)
                .value(t -> assertThat(t).extracting(LoanDto::getBorrowerName).containsExactly("Borrower One", "Borrower Two"));
    }

    @Test
    @DisplayName("List loan history by non exist loan")
    void testGetLoanHistoryForBook_Empty() {
        Book newBook = bookRepository.save(new Book("9999","New book", 2000, authorOne));
        webTestClient.get()
                .uri("/api/loans/history/{id}", newBook.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("List loan history by non exists book")
    void testGetLoanHistoryForBook_BookNotFound() {
        webTestClient.get()
                .uri("/api/loans/history/{id}", -1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Checkout book")
    void testCheckoutBook() {
        CheckoutRequestDto command = new CheckoutRequestDto(bookThreeDto.getId(), "Borrower New");
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanDto.class)
                .value(t-> {
                    assertThat(t.getBorrowerName()).isEqualTo(command.getBorrowerName());
                    assertThat(t.getLoanDate()).isEqualTo(LocalDate.now());
                });

        webTestClient.get()
                .uri("/api/loans")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanDto.class)
                .hasSize(4);
    }

    @Test
    @DisplayName("Checkout book borrower name empty")
    void testCheckoutBook_BorrowerEmpty() {
        CheckoutRequestDto command = new CheckoutRequestDto(bookThreeDto.getId(), "");
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Checkout book missing book")
    void testCheckoutBook_BookError() {
        CheckoutRequestDto command = new CheckoutRequestDto(-1L, "Borrower New");
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(command)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Checkout book already loaned")
    void testCheckoutBook_LoanError() {
        CheckoutRequestDto command = new CheckoutRequestDto(bookTwoDto.getId(), "Borrower New");
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(command)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict");
    }

    @Test
    @DisplayName("Return book")
    void testReturnBook() {
         webTestClient.put()
                .uri("/api/loans/book/{bookId}/return", bookTwoDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanDto.class)
                .value(t-> {
                    assertThat(t.getReturnDate()).isEqualTo(LocalDate.now());
                });
    }

    @Test
    @DisplayName("Return non exists book")
    void testReturnBook_BookIdError() {
        webTestClient.put()
                .uri("/api/loans/book/{bookId}/return", -1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Return book but no loan")
    void testReturnBook_NoLoanError() {
        Loan entity = loanRepository.findByBookIdAndReturnDateIsNull(bookTwoDto.getId()).get();
        entity.setReturnDate(LocalDate.now().minusDays(1));
        loanRepository.saveAndFlush(entity);

        webTestClient.put()
                .uri("/api/loans/book/{bookId}/return", bookTwoDto.getId())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }
}
