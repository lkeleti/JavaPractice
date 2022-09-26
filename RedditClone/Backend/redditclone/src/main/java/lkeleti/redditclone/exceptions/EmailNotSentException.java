package lkeleti.redditclone.exceptions;

import org.springframework.mail.MailException;

public class EmailNotSentException extends RuntimeException {
    public EmailNotSentException(String message, MailException mailException) {
        /*super(
                URI.create("/api/email-send-error"),
                message,
                Status.BAD_REQUEST,
                String.format(mailException.getMessage())
        );*/
        super(String.format(mailException.getMessage()));
    }
}
