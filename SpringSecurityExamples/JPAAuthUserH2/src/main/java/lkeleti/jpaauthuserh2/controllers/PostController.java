package lkeleti.jpaauthuserh2.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lkeleti.jpaauthuserh2.models.PostDto;
import lkeleti.jpaauthuserh2.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@SecurityRequirement(name = "jpaauthuserh2")
public class PostController {

    private final PostService postService;

    @GetMapping
    public List<PostDto> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostDto findById(@PathVariable long id) {
        return postService.findById(id);
    }
}
