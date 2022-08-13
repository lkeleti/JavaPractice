package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.CommentDto;
import lkeleti.redditclone.dtos.CreateCommentCommand;
import lkeleti.redditclone.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody CreateCommentCommand createCommentCommand) {
        return commentService.createComment(createCommentCommand);
    }

    @GetMapping("/by-postId/{postId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<CommentDto> getAllCommentsForPost(@PathVariable long postId) {
        return commentService.getAllCommentsForPost(postId);
    }

    @GetMapping("/by-user/{userName}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<CommentDto> getAllCommentsByUsername(@PathVariable String username) {
        return commentService.getAllCommentsByUsername(username);
    }
}
