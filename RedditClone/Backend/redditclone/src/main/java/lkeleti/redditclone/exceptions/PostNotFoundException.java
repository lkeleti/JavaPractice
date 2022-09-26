package lkeleti.redditclone.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(long id) {
        /*super(
                URI.create("/api/post-not-found"),
                "Post not found",
                Status.NOT_FOUND,
                String.format("Post not found by: %d id.", id)
        );*/
        super(String.format("Post not found by: %d id.", id));
    }
}
