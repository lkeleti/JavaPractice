package lkeleti.redditclone.exceptions;

public class UserNameNotFoundException extends RuntimeException {
    public UserNameNotFoundException(String username) {
        /*super(
                URI.create("/api/username-not-found"),
                "Username not found",
                Status.NOT_FOUND,
                String.format("Username not found: %s", username)
        );*/
        super(String.format("Username not found: %s", username));
    }
}
