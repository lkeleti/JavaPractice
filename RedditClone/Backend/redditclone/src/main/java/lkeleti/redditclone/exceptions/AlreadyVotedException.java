package lkeleti.redditclone.exceptions;

import lkeleti.redditclone.models.VoteType;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class AlreadyVotedException extends AbstractThrowableProblem {
    public AlreadyVotedException(VoteType voteType) {
        super(
                URI.create("/api/voted-already"),
                "Already voted.",
                Status.NOT_ACCEPTABLE,
                String.format("Already voted: %s id.", voteType)
        );
    }
}
