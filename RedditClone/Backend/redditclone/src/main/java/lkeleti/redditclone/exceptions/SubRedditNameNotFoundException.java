package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class SubRedditNameNotFoundException extends AbstractThrowableProblem {
    public SubRedditNameNotFoundException(String subRedditName) {
        super(
                URI.create("/api/subredditname-not-found"),
                "Subreddit not found by name",
                Status.NOT_FOUND,
                String.format("Subreddit not found by name: %s.", subRedditName)
        );
    }
}
