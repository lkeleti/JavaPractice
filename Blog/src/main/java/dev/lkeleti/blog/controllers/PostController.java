package dev.lkeleti.blog.controllers;

import dev.lkeleti.blog.models.PostDto;
import dev.lkeleti.blog.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<PostDto> listAllPosts() {
        return postService.listAllPosts();
    }

    @GetMapping("/{id}")
    public PostDto listPostById(@PathVariable long id) {
        return postService.listPostById(id);
    }
}
