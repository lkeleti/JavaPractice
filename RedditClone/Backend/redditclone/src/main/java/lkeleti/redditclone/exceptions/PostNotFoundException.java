package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class PostNotFoundException extends AbstractThrowableProblem {
    public PostNotFoundException(long id) {
        super(
                URI.create("/api/post-not-found"),
                "Post not found",
                Status.NOT_FOUND,
                String.format("Post not found by: %d id.", id)
        );
    }
}
