package lkeleti.redditclone.dtos;

import lkeleti.redditclone.models.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoteDto {
    private Long voteId;
    private VoteType voteType;
    private PostDto post;
    private UserDto user;
}
