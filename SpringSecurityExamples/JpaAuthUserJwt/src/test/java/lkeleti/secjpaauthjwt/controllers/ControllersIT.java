package lkeleti.secjpaauthjwt.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void postTokenWithValidAdmin() {
        webTestClient.post()
                .uri("/api/token")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void postTokenWithValidUser() {
        webTestClient.post()
                .uri("/api/token")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void postTokenWithInvalidData() {
        webTestClient.post()
                .uri("/api/token")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin1", "password"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getHomeWithInvalidData() {
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin1", "password"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getHomeWithValidAdmin() {
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Hello admin!");
    }

    @Test
    void getHomeWithValidUser() {
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Hello user!");
    }

    @Test
    void getHomeWithValidAdminToken() {
        String token = webTestClient.post()
                .uri("/api/token")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Hello admin!");
    }

    @Test
    void getHomeWithValidUserToken() {
        String token = webTestClient.post()
                .uri("/api/token")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Hello user!");
    }

    @Test
    void getHomeWithInvalidTestToken() {
        //ToDo Check user is really valid and not expired the token?
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoidGVzdCIsImV4cCI6MTY2NDE0MDU2NSwiaWF0IjoxNjY0MTM2OTY1LCJzY29wZSI6IkFETUlOIn0.Yo1pIENLkAyLdN9pwgZhXZIVho6VY3b_MhZYDfssuRW_CqQikk25mVddmqAG6e9JH-fBoe-2bOSOcPlZXTCiS8u2MNw3pcWr9kOtqguKjJ6RqMfS0iyJ01sqU7m8ROD8GFwtlFoRWhYPXfGz6Ag1Kh0_6DvggqKm9CX49k6VZx9gB-tUvlWUks0f2mwqES4l4hekTlVknxHM0mZkUgf4zF8imm2DlzmhIIMZ9a8kk8tmu2BHjDNUHwv01cYlgD9qoQUMgmm-XpCjESDfNf8F1bUwsrcTv8_AmWrjzf2CGYRMBVk5WpMvIc1V-w0pKZblNhEVWxS6OJN1_p5Ioq9Bfw";
        webTestClient.get()
                .uri("/api/admin")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Hello test!");
    }
}