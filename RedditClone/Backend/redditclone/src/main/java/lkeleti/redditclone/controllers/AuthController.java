package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.*;
import lkeleti.redditclone.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto signUp(@Valid @RequestBody RegisterRequestCommand registerRequestCommand) {
        authService.signUp(registerRequestCommand);
        return new MessageDto("User registration successful");
    }

    @GetMapping("/accountVerification/{token}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto verifyAccount(@PathVariable String token) {
        return authService.verifyAccount(token);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationResponse login(@Valid @RequestBody LoginRequestCommand loginRequestCommand) {
        return authService.login(loginRequestCommand);
    }

    @PostMapping("/refresh/token")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenCommand refreshTokenCommand) {
        return authService.refreshTokens(refreshTokenCommand);
    }
}
