package spring.project.urlShortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.urlShortener.models.entities.Url;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsUrlByShortenedUrlString(String s);

    Url findByShortenedUrlString(String shortenedUrlString);

    boolean existsUrlByLongUrl(String longUrl);

    Optional<Url> findByLongUrl(String longUrl);
}
