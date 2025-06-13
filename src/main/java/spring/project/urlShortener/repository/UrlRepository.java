package spring.project.urlShortener.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.urlShortener.models.entities.Url;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsUrlByShortenedUrlStringAndIsDeletedIsFalse(String s);

    Optional<Url> findByShortenedUrlStringAndIsDeletedIsFalse(String shortenedUrlString);

    Page<Url> findAllByIsDeletedIsFalse(Pageable pageable);

    Optional<Url> findByLongUrlAndIsDeletedIsFalse(String longUrl);

    Optional<Url> findByIdAndIsDeletedIsFalse(Long id);
}
