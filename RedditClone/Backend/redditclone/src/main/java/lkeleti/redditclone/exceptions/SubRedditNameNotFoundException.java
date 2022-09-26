package lkeleti.redditclone.exceptions;

public class SubRedditNameNotFoundException extends RuntimeException {
    public SubRedditNameNotFoundException(String subRedditName) {
        /*super(
                URI.create("/api/subredditname-not-found"),
                "Subreddit not found by name",
                Status.NOT_FOUND,
                String.format("Subreddit not found by name: %s.", subRedditName)
        );*/
        super(String.format("Subreddit not found by name: %s.", subRedditName));
    }
}
