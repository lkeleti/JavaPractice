package lkeleti.redditclone.controllers;

import lkeleti.redditclone.dtos.CreateVoteCommand;
import lkeleti.redditclone.dtos.VoteDto;
import lkeleti.redditclone.services.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteDto createVote(@Valid @RequestBody CreateVoteCommand createVoteCommand) {
        return voteService.createVote(createVoteCommand);
    }
}
