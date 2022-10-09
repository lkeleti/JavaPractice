package dev.lkeleti.blog.repositories;

import dev.lkeleti.blog.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
