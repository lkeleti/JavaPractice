package lkeleti.jpaauthuserh2.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class PostNotFoundException extends AbstractThrowableProblem {
    public PostNotFoundException(Long id) {
        super(
                URI.create("/api/posts/not-found"),
                "Post by id not found!",
                Status.NOT_FOUND,
                String.format("Post by id: %s not found!", id)
        );
    }
}
