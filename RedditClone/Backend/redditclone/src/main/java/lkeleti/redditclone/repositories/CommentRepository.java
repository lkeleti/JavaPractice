package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Comment;
import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByUser(User user);
}
