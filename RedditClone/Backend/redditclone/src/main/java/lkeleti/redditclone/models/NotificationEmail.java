package lkeleti.redditclone.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationEmail {
    private String subject;
    private String recipient;
    private String body;
}
