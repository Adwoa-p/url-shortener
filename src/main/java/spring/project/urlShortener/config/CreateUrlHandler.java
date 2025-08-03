package spring.project.urlShortener.config;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.BadRequestException;
import spring.project.urlShortener.models.dtos.RandomUrlRequest;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.CustomUrlRequest;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;

import java.util.Optional;

@Service
public class CreateUrlHandler {
    public final URLValidator urlValidator;
    public final UrlRepository urlRepository;
    public final StringGenerator stringGenerator;

    public CreateUrlHandler(UrlRepository urlRepository, URLValidator urlValidator, StringGenerator stringGenerator) {
        this.urlRepository = urlRepository;
        this.urlValidator = urlValidator;
        this.stringGenerator = stringGenerator;
    }

    public ResponseDto<Url> createUrlHandler(@Nullable CustomUrlRequest customUrlRequest, @Nullable RandomUrlRequest randomUrlRequest) {
        if ((customUrlRequest == null && randomUrlRequest == null) || (customUrlRequest != null && randomUrlRequest != null)) {
            throw new BadRequestException("Only one request type must be provided (custom or random).");
        }

        String longUrl;

        if (customUrlRequest != null) {
            longUrl = customUrlRequest.getLongUrl();
        } else {
            longUrl = randomUrlRequest.getLongUrl();
        }

        if (!urlValidator.isValidUrl(longUrl)) {
            throw new BadRequestException("Invalid URL");
        }

        Optional<Url> existingUrl = urlRepository.findByLongUrlAndIsDeletedIsFalse(longUrl);
        if (existingUrl.isPresent()) {
            return ResponseDto.<Url>builder()
                    .message(String.format("ShortUrl with id %d already created", existingUrl.get().getId()))
                    .response(existingUrl.get())
                    .build();
        }

        Optional<Url> existingDeletedUrl = urlRepository.findByLongUrlAndIsDeletedIsTrue(longUrl);
        if (existingDeletedUrl.isPresent()) {
            existingDeletedUrl.get().setIsDeleted(false);
            urlRepository.save(existingDeletedUrl.get());
            return ResponseDto.<Url>builder()
                    .message(String.format("ShortUrl with id %d created", existingDeletedUrl.get().getId()))
                    .response(existingDeletedUrl.get())
                    .build();
        }

        String shortUrlString;
        if (customUrlRequest != null) {
            shortUrlString = customUrlRequest.getCustomUrlString();
        } else {
            do {
                shortUrlString = stringGenerator.generateString();
            } while (urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(shortUrlString));
        }

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortenedUrlString(shortUrlString);
        urlRepository.save(url);

        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                .response(url)
                .build();
    }

    public ResponseDto<Url> createUrlHandler(CustomUrlRequest customUrlRequest) {
        return createUrlHandler(customUrlRequest, null);
    }

    public ResponseDto<Url> createUrlHandler(RandomUrlRequest randomUrlRequest) {
        return createUrlHandler(null, randomUrlRequest);
    }
}
