package lkeleti.redditclone.exceptions;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String token) {
        /*super(
                URI.create("/api/refreshtoken-not-found"),
                "Invalid refresh token.",
                Status.NOT_FOUND,
                String.format("Invalid refresh token: %s.", token)
        );*/
        super(String.format("Invalid refresh token: %s.", token));
    }
}
