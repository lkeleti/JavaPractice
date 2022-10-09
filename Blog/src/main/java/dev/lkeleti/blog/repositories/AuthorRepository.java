package dev.lkeleti.blog.repositories;

import dev.lkeleti.blog.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
