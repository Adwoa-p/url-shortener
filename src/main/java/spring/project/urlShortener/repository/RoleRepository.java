package spring.project.urlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.urlShortener.models.entities.Role;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String admin);
}
