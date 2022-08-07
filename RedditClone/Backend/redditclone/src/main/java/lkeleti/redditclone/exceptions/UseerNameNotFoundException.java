package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UseerNameNotFoundException extends AbstractThrowableProblem {
    public UseerNameNotFoundException(String username) {
        super(
                URI.create("/api/username-not-found"),
                "Username not found",
                Status.BAD_REQUEST,
                String.format("Username not found: %s", username)
        );
    }
}
