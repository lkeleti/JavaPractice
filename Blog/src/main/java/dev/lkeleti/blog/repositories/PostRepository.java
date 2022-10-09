package dev.lkeleti.blog.repositories;

import dev.lkeleti.blog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
