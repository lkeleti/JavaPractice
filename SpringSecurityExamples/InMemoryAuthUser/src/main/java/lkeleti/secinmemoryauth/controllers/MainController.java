package lkeleti.secinmemoryauth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/home")
    public String getHome() {
        return "This is home page!";
    }

    @GetMapping("/user")
    public String getUser() {
        return "This is user page!";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "This is admin page!";
    }
}
