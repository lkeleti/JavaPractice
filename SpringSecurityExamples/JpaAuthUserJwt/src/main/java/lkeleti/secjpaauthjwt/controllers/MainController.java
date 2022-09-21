package lkeleti.secjpaauthjwt.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/home")
    public String getHome(Principal principal) {
        return "Hello " + principal.getName() + '!';
    }
}
