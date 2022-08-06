package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
