package lkeleti.jpaauthuserh2.repositories;

import lkeleti.jpaauthuserh2.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
