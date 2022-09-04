package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class RefreshTokenNotFoundException extends AbstractThrowableProblem {
    public RefreshTokenNotFoundException(String token) {
        super(
                URI.create("/api/refreshtoken-not-found"),
                "Invalid refresh token.",
                Status.NOT_FOUND,
                String.format("Invalid refresh token: %s.", token)
        );
    }
}
