package spring.project.urlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.urlShortener.models.entities.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
}
