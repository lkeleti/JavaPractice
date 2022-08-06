package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
