package lkeleti.redditclone.repositories;

import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
}
