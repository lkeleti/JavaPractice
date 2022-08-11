package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.CreateSubRedditCommand;
import lkeleti.redditclone.dtos.SubRedditDto;
import lkeleti.redditclone.services.SubRedditService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
public class SubRedditController {

    private SubRedditService subRedditService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubRedditDto createSubReddit(@Valid @RequestBody CreateSubRedditCommand createSubRedditCommand) {
        return subRedditService.createSubReddit(createSubRedditCommand);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<SubRedditDto> getAllSubReddits() {
        return subRedditService.getAllSubReddits();
    }
}
