package lkeleti.redditclone.exceptions;

public class SubRedditNotFoundException extends RuntimeException {
    public SubRedditNotFoundException(long id) {
        /*super(
                URI.create("/api/subreddit-not-found"),
                "Subreddit not found",
                Status.NOT_FOUND,
                String.format("Subreddit not found by: %d id.", id)
        );*/
        super(String.format("Subreddit not found by: %d id.", id));
    }
}
