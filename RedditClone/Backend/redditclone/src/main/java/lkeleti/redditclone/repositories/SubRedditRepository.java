package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubRedditRepository extends JpaRepository<SubReddit, Long> {
    Optional<SubReddit> findByName(String subRedditName);
}
