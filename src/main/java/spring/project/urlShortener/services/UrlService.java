package spring.project.urlShortener.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.config.StringGenerator;
import spring.project.urlShortener.config.URLValidator;
import spring.project.urlShortener.exceptions.ResourceAlreadyExistsException;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Optional;


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
            throw new ResourceNotFoundException("Invalid Url");
        }
        Optional<Url> existingUrl = urlRepository.findByLongUrlAndIsDeletedIsFalse(urlDto.getLongUrl());
        if (existingUrl.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    String.format("The short url for %s already exists", urlDto.getLongUrl())
            );
        }
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        String urlString = stringGenerator.generateString();
        boolean shortUrlString = urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(urlString);
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
        Optional<Url> existingUrl = urlRepository.findByLongUrlAndIsDeletedIsFalse(urlDto.getLongUrl());
        if (existingUrl.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    String.format("The short url for %s already exists", urlDto.getLongUrl())
            );
        }
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        url.setShortenedUrlString(urlDto.getCustomUrlString());
        boolean customUrlString = urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(urlDto.getCustomUrlString());
        if (customUrlString) {
            return ResponseDto.<Url>builder()
                    .message("Custom Url already exists. Try again with another")
                    .build();
        }
        urlRepository.save(url);
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                .response(url)
                .build();
    }

    public ResponseDto<Url> redirect(String shortenedUrlString) {
        Url longUrl = urlRepository.findByShortenedUrlStringAndIsDeletedIsFalse(shortenedUrlString)
                .orElseThrow(() -> new ResourceNotFoundException("Url not found, it's invalid"));
        if (LocalDateTime.now().isAfter(longUrl.getExpiresAt()) || !urlValidator.isValidUrl(longUrl.getLongUrl())) {
            longUrl.setIsExpired(true);
            urlRepository.save(longUrl);
            return ResponseDto.<Url>builder()
                    .message("URL not found, it's invalid or expired")
                    .build();
        }
        return ResponseDto.<Url>builder()
                .message("Redirecting to long url")
                .response(longUrl)
                .build();
    }

    public Page<Url> getAllUrls(int pageNo, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return urlRepository.findAllByIsDeletedIsFalse(pageable);
    }

    public ResponseDto<Url> getUrl(Long id) {
        Url longUrl = urlRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        return ResponseDto.<Url>builder()
                .message("Returning long url by ID")
                .response(longUrl)
                .build();
    }

    public ResponseDto<String> updateUrl(Long id, UrlDto urlDto) {
        Url longUrl = urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        longUrl.setLongUrl(urlDto.getLongUrl());
        longUrl.setShortenedUrlString(urlDto.getCustomUrlString());
        urlRepository.save(longUrl);
        return ResponseDto.<String>builder()
                .message(String.format( "Successfully updated url with id %d",id))
                .build();
    }

    public ResponseDto<String> deleteUrl(Long id) {
        Url longUrl = urlRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        longUrl.setIsDeleted(true);
        urlRepository.save(longUrl);
        return ResponseDto.<String>builder()
                .message(String.format( "Successfully deleted url with id %d",id))
                .build();
    }
}
