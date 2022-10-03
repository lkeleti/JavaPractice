package lkeleti.redditclone.exceptions;

import org.springframework.mail.MailException;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class EmailNotSentException extends AbstractThrowableProblem {
    public EmailNotSentException(String message, MailException mailException) {
        super(
                URI.create("/api/email-send-error"),
                message,
                Status.BAD_REQUEST,
                String.format(mailException.getMessage())
        );
    }
}
