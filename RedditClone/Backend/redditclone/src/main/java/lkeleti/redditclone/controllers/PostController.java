package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.CreatePostCommand;
import lkeleti.redditclone.dtos.PostDto;
import lkeleti.redditclone.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@Valid @RequestBody CreatePostCommand createPostCommand) {
        return postService.createPost(createPostCommand);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PostDto getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/by-subreddit/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PostDto> getPostsBySubRedditId(@PathVariable long id) {
        return postService.getPostsBySubRedditId(id);
    }

    @GetMapping("/by-user/{name}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PostDto> getPostsByUser(@PathVariable String username) {
        return postService.getPostsByUser(username);
    }

}
