package spring.project.urlShortener.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.repository.UrlRepository;

@Service
@AllArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
}
