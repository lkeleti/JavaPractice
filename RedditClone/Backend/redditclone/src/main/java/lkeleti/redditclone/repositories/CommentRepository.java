package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
