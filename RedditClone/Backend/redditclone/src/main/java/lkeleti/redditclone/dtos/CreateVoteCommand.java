package lkeleti.redditclone.dtos;

import lkeleti.redditclone.models.VoteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateVoteCommand {
    @NotBlank
    private VoteType voteType;
    @Positive
    private Long postId;
}
