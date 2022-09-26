package lkeleti.redditclone.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        /*super(
                URI.create("/api/invalid-token"),
                "Invalid token!",
                Status.BAD_REQUEST,
                "Invalid token!"
        );*/
        super("Invalid token!");
    }

}
