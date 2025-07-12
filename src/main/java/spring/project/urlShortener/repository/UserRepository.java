package spring.project.urlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.urlShortener.models.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
