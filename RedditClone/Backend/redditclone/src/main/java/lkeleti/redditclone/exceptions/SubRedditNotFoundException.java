package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class SubRedditNotFoundException extends AbstractThrowableProblem {
    public SubRedditNotFoundException(long id) {
        super(
                URI.create("/api/subreddit-not-found"),
                "Subreddit not found",
                Status.NOT_FOUND,
                String.format("Subreddit not found by: %d id.", id)
        );
    }
}
