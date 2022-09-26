package lkeleti.redditclone.exceptions;

import lkeleti.redditclone.models.VoteType;
public class AlreadyVotedException extends RuntimeException {
    public AlreadyVotedException(VoteType voteType) {
        /*super(
                URI.create("/api/voted-already"),
                "Already voted.",
                Status.NOT_ACCEPTABLE,
                String.format("Already voted: %s id.", voteType)
        );*/
        super(String.format("Already voted: %s id.", voteType));
    }
}
