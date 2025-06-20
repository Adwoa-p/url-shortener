package spring.project.urlShortener.config;

import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.BadRequestException;
import spring.project.urlShortener.exceptions.ResourceAlreadyExistsException;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;

import java.util.Optional;

@Service
public class CreateUrlHandler {
    public  URLValidator urlValidator;
    public UrlRepository urlRepository;
    public StringGenerator stringGenerator;

    public ResponseDto<Url> createUrlHandler(UrlDto urlDto) {
        if (!urlValidator.isValidUrl(urlDto.getLongUrl())) {
            throw new BadRequestException("Invalid Url");
        }
        Optional<Url> existingUrl = urlRepository.findByLongUrlAndIsDeletedIsFalse(urlDto.getLongUrl());
        if (existingUrl.isPresent()) {
            throw new ResourceAlreadyExistsException(
                    String.format("The short url for %s already exists", urlDto.getLongUrl())
            );
        }
        String randomUrlString = stringGenerator.generateString();
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        url.setShortenedUrlString(randomUrlString);
        if (urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(randomUrlString)) {
           throw new ResourceAlreadyExistsException("Short Url String is already taken");
        }
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                        .response(url)
                        .build();
    }

    public ResponseDto<Url> createCustomUrlHandler(UrlDto urlDto) {
        if (!urlValidator.isValidUrl(urlDto.getLongUrl())) {
            throw new BadRequestException("Invalid Url");
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
        if (urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(urlDto.getCustomUrlString())) {
            throw new ResourceAlreadyExistsException("Short Url String is already taken");
        }
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                .response(url)
                .build();
    }

}
