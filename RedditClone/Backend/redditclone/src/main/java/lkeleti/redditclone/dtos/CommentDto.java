package lkeleti.redditclone.dtos;

import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private User user;
    private Post post;
}
