package lkeleti.secinmemoryauth.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerIT {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void getHomeTest() {
        webTestClient.get()
                .uri("/api/home")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("This is home page!");
    }

    @Test
    void getUserNotLoggedInTest() {
        webTestClient.get()
                .uri("/api/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUserLoggedInUserTest() {
        webTestClient.get()
                .uri("/api/user")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("This is user page!");
    }

    @Test
    void getUserLoggedInAdminTest() {
        webTestClient.get()
                .uri("/api/user")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getAdminNotLoggedInTest() {
        webTestClient.get()
                .uri("/api/admin")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getAdminLoggedInAdminTest() {
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("This is admin page!");
    }

    @Test
    void getAdminLoggedInUserTest() {
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isForbidden();
    }
}