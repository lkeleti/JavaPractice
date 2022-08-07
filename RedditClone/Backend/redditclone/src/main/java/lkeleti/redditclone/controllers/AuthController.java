package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.MessageDto;
import lkeleti.redditclone.dtos.RegisterRequestCommand;
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
    public MessageDto verifyAccount(@PathVariable(required = true) String token) {
        return authService.verifyAccount(token);
    }

}
