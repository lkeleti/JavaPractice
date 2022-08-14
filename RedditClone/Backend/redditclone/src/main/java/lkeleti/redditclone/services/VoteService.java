package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.CreateVoteCommand;
import lkeleti.redditclone.dtos.VoteDto;
import lkeleti.redditclone.exceptions.AlreadyVotedException;
import lkeleti.redditclone.exceptions.PostNotFoundException;
import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.models.Vote;
import lkeleti.redditclone.models.VoteType;
import lkeleti.redditclone.repositories.PostRepository;
import lkeleti.redditclone.repositories.VoteRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final ModelMapper modelMapper;
    private final VoteRepository voteRepository;
    public final PostRepository postRepository;
    public final AuthService authService;

    @Transactional
    public VoteDto createVote(CreateVoteCommand createVoteCommand) {
        Long postId = createVoteCommand.getPostId();
        Post post = postRepository.findById(postId).orElseThrow(
                ()->new PostNotFoundException(postId)
        );

        User user = authService.getCurrentUser();
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, user);
        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(createVoteCommand.getVoteType())) {
            throw new AlreadyVotedException(createVoteCommand.getVoteType());
        }

        if (VoteType.UP_VOTE.equals(createVoteCommand.getVoteType())) {
            post.setVoteCount(post.getVoteCount()+1);
        }

        if (VoteType.DOWN_VOTE.equals(createVoteCommand.getVoteType())) {
            post.setVoteCount(post.getVoteCount()-1);
        }
        Vote vote = new Vote(
          createVoteCommand.getVoteType(),
          post,
          user
        );
        return modelMapper.map(voteRepository.save(vote), VoteDto.class);
    }
}
