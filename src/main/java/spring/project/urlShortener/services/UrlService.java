package spring.project.urlShortener.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.config.URLValidator;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.dtos.RandomUrlRequest;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.CustomUrlRequest;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.repository.UrlRepository;
import spring.project.urlShortener.config.CreateUrlHandler;
import java.time.LocalDateTime;


@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final URLValidator urlValidator;
    private final CreateUrlHandler check;
    private final UserService userService;

    public UrlService(UrlRepository urlRepository, URLValidator urlValidator, CreateUrlHandler check, UserService userService) {
        this.urlRepository = urlRepository;
        this.urlValidator = urlValidator;
        this.check = check;
        this.userService = userService;
    }

    public ResponseDto<Url> createUrl(RandomUrlRequest randomUrlRequest) {
        return check.createUrlHandler(randomUrlRequest);

    }

    public ResponseDto<Url> createCustomUrl(CustomUrlRequest customUrlRequest) {
        return check.createUrlHandler(customUrlRequest);
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

    public ResponseDto<String> updateUrl(Long id, CustomUrlRequest customUrlRequest) {
        Url longUrl = urlRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        longUrl.setLongUrl(customUrlRequest.getLongUrl());
        longUrl.setShortenedUrlString(customUrlRequest.getCustomUrlString());
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

    public Page<Url> getAllMyUrls(int pageNo, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        User user = userService.getAuthenticatedUser();
        return urlRepository.findAllByUser(user,pageable);
    }

    public ResponseDto<Url> getUrlById(Long id) {
        User user = userService.getAuthenticatedUser();

        Url longUrl = urlRepository.findByIdAndIsDeletedIsFalseAndUser(id,user)

                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        return ResponseDto.<Url>builder()
                .message("Returning long url")
                .response(longUrl)
                .build();
    }
}
