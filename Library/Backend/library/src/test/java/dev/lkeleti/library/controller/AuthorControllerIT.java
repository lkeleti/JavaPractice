package dev.lkeleti.library.controller;

import dev.lkeleti.library.dto.AuthorDto;
import dev.lkeleti.library.dto.CreateAuthorCommand;
import dev.lkeleti.library.dto.UpdateAuthorCommand;
import dev.lkeleti.library.model.Author;
import dev.lkeleti.library.repository.AuthorRepository;
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
class AuthorControllerIT {


    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    WebTestClient webTestClient;


    AuthorDto authorOneDto;
    AuthorDto authorTwoDto;

    @BeforeEach
    void init() {
        Author authorOne = authorRepository.save(new Author("John Doe",1970,"Magyar"));
        Author authorTwo = authorRepository.save(new Author("Jack Doe",1980,"Német"));

        authorOneDto = new AuthorDto(authorOne.getId(), authorOne.getName(), authorOne.getBirthYear(), authorOne.getNationality(), new ArrayList<>());
        authorTwoDto = new AuthorDto(authorTwo.getId(), authorTwo.getName(), authorTwo.getBirthYear(), authorTwo.getNationality(), new ArrayList<>());
    }

    @Test
    @DisplayName("List all authors")
    void testListAllAuthors() {
        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(2)
                .value(t -> assertThat(t).extracting(AuthorDto::getName).containsExactly("John Doe", "Jack Doe"));
    }

    @Test
    @DisplayName("List author by ID")
    void testListAuthorById() {
        webTestClient.get()
                .uri("/api/authors/{id}", authorOneDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .value(t -> {
                    assertThat(t.getId()).isEqualTo(authorOneDto.getId());
                    assertThat(t.getName()).isEqualTo(authorOneDto.getName());
                    assertThat(t.getBirthYear()).isEqualTo(authorOneDto.getBirthYear());
                    assertThat(t.getNationality()).isEqualTo(authorOneDto.getNationality());
                });
    }

    @Test
    @DisplayName("Testing get author by ID with invalid value")
    void testGetAuthorByInvalidId() {
        webTestClient.get()
                .uri("/api/authors/{id}", -1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Test create new author")
    void testCreateNewAuthor() {
        CreateAuthorCommand command = new CreateAuthorCommand("Jane Doe",2000,"Holland");

        webTestClient.post()
                .uri("/api/authors")
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AuthorDto.class)
                .value(t-> {
                    assertThat(t.getName()).isEqualTo(command.getName());
                    assertThat(t.getBirthYear()).isEqualTo(command.getBirthYear());
                    assertThat(t.getNationality()).isEqualTo(command.getNationality());
                    assertThat(t.getId()).isNotNull();
                });

        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(3);
    }

    @Test
    @DisplayName("Test create new author with blank name")
    void testCreateNewAuthor_error() {
        CreateAuthorCommand command = new CreateAuthorCommand("",2000,"Holland");

        webTestClient.post()
                .uri("/api/authors")
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Test modify author name")
    void testModifyAuthorName() {
        UpdateAuthorCommand updateAuthorCommand = new UpdateAuthorCommand("Jane Doe",2000,"Belga");

        AuthorDto author =
                webTestClient.put()
                        .uri("/api/authors/{id}", authorOneDto.getId())
                        .bodyValue(updateAuthorCommand)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthorDto.class)
                        .returnResult().getResponseBody();
        assertThat(author.getName()).isEqualTo("Jane Doe");
        assertThat(author.getBirthYear()).isEqualTo(2000);
        assertThat(author.getNationality()).isEqualTo("Belga");
    }

    @Test
    @DisplayName("Test modify author with blank name")
    void testModifyAuthorInvalidName() {
        UpdateAuthorCommand updateAuthorCommand = new UpdateAuthorCommand("",2000,"Belga");

        webTestClient.put()
                .uri("/api/authors/{id}", authorOneDto.getId())
                .bodyValue(updateAuthorCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Test modify not exists author")
    void testModifyAuthorInvalidId() {
        UpdateAuthorCommand updateAuthorCommand = new UpdateAuthorCommand("John Doe",2000,"Belga");

        webTestClient.put()
                .uri("/api/authors/{id}", -1L)
                .bodyValue(updateAuthorCommand)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Test delete author")
    void testDeleteAuthor() {
        webTestClient.delete()
                .uri("/api/authors/{id}", authorOneDto.getId())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("Test delete not exists author")
    void testDeleteAuthorInvalidId() {
        webTestClient.delete()
                .uri("/api/authors/{id}", -1L)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(2);
    }
}