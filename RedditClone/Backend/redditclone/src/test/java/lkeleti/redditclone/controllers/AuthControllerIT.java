package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.AuthenticationResponse;
import lkeleti.redditclone.dtos.LoginRequestCommand;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    UserRepository userRepository;

    @Test
    void loginWithInvalidUsernameTest() {
        List<User> usersList = userRepository.findAll();
        String username = usersList.get(0).getUserName() + "!";

        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue(
                        new LoginRequestCommand(username, "password")
                )
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(RuntimeException.class)
                .value(p->assertThat(p.getMessage()).isEqualTo("Username not found: " + username));
    }

    @Test
    void loginWithInvalidPasswordTest() {
        List<User> usersList = userRepository.findAll();
        String username = usersList.get(0).getUserName();

        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue(
                        new LoginRequestCommand(username, "password!")
                )
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(RuntimeException.class)
                .value(p->assertThat(p.getMessage()).isEqualTo("Username not found: " + username));
    }

    @Test
    void ValidDataTest() {
        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue(
                        new LoginRequestCommand("lkeleti", "password")
                )
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(AuthenticationResponse.class)
                .value(a->assertThat(a.getUsername()).isEqualTo("lkeleti"));
    }
}