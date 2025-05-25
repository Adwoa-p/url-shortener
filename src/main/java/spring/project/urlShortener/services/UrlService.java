package spring.project.urlShortener.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import spring.project.urlShortener.config.StringGenerator;
import spring.project.urlShortener.config.URLValidator;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;

import java.time.LocalDateTime;


@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final StringGenerator stringGenerator;
    private final URLValidator urlValidator;

    @Autowired
    public UrlService(UrlRepository urlRepository, StringGenerator stringGenerator, URLValidator urlValidator) {
        this.urlRepository = urlRepository;
        this.stringGenerator = stringGenerator;
        this.urlValidator = urlValidator;
    }

    public ResponseDto<Url> createUrl(UrlDto urlDto) {
        if (!urlValidator.isValidUrl(urlDto.getLongUrl())) {
            throw new ResourceNotFoundException("Url not found, it's invalid");
        }
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        String urlString = stringGenerator.generateString();
        boolean shortUrlString = urlRepository.existsUrlByShortenedUrlString(urlString);
        if (shortUrlString) {
            return ResponseDto.<Url>builder()
                    .message("ShortUrl already exists. Try again with another")
                    .build();
        }
        url.setShortenedUrlString(urlString);
        urlRepository.save(url);
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                .response(url)
                .build();
    }

    public ResponseDto<Url> createCustomUrl(UrlDto urlDto){
        if (!urlValidator.isValidUrl(urlDto.getLongUrl())) {
            throw new ResourceNotFoundException("Url not found, it's invalid");
        }
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        url.setShortenedUrlString(urlDto.getCustomUrlString());
        boolean customUrlString = urlRepository.existsUrlByShortenedUrlString(urlDto.getCustomUrlString());
        if (customUrlString) {
            return ResponseDto.<Url>builder()
                    .message("ShortUrl already exists. Try again with another")
                    .build();
        }
        urlRepository.save(url);
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                .response(url)
                .build();
    }


    public RedirectView getUrl(String shortenedUrlString) {
        Url longUrl = urlRepository.findByShortenedUrlString(shortenedUrlString);
        if (LocalDateTime.now().isAfter(longUrl.getExpiresAt())) {
            longUrl.setIsExpired(true);
            urlRepository.save(longUrl);
            return new RedirectView("/error");
        }
        if (urlValidator.isValidUrl(longUrl.getLongUrl())) {
            return new RedirectView(longUrl.getLongUrl());
        } else {
            return new RedirectView("/error");
        }
    }
}
