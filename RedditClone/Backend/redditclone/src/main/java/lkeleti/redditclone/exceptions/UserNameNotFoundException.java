package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserNameNotFoundException extends AbstractThrowableProblem {
    public UserNameNotFoundException(String username) {
        super(
                URI.create("/api/username-not-found"),
                "Username not found",
                Status.NOT_FOUND,
                String.format("Username not found: %s", username)
        );
    }
}
