package lkeleti.redditclone.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvalidTokenException extends AbstractThrowableProblem {

    public InvalidTokenException() {
        super(
                URI.create("/api/invalid-token"),
                "Invalid token!",
                Status.BAD_REQUEST,
                "Invalid token!"
        );
    }

}
