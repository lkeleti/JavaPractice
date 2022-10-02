package lkeleti.jpaauthuserh2.repositories;


import lkeleti.jpaauthuserh2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
