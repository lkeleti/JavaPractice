package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.SubReddit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubRedditRepository extends JpaRepository<SubReddit, Long> {
}
