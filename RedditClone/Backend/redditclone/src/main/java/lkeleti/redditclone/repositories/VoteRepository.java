package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

    List<Vote> findAllByPost(Post post);
}
