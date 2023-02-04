package dev.lkeleti.jwtsymmetrickey.controllers;

import dev.lkeleti.jwtsymmetrickey.services.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final TokenService service;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return service.generateToken(authentication);
    }

}
