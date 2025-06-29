package spring.project.urlShortener.config;

import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.BadRequestException;
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

    public CreateUrlHandler(UrlRepository urlRepository, URLValidator urlValidator, StringGenerator stringGenerator) {
        this.urlRepository = urlRepository;
        this.urlValidator = urlValidator;
        this.stringGenerator = stringGenerator;
    }

    public ResponseDto<Url> createUrlHandler(UrlDto urlDto) {
        if (!urlValidator.isValidUrl(urlDto.getLongUrl())) {
            throw new BadRequestException("Invalid Url");
        }
        Optional<Url> existingUrl = urlRepository.findByLongUrlAndIsDeletedIsFalse(urlDto.getLongUrl());
        if (existingUrl.isPresent()) {
            return ResponseDto.<Url>builder()
                    .message(String.format("ShortUrl with id %d already created", existingUrl.get().getId()))
                    .response(existingUrl.get())
                    .build();
        }
        Optional<Url> existingDeletedUrl = urlRepository.findByLongUrlAndIsDeletedIsTrue(urlDto.getLongUrl());
        if (existingDeletedUrl.isPresent()) {
            existingDeletedUrl.get().setIsDeleted(false);
            urlRepository.save(existingDeletedUrl.get());
            return ResponseDto.<Url>builder()
                    .message(String.format("ShortUrl with id %d created", existingDeletedUrl.get().getId()))
                    .response(existingDeletedUrl.get())
                    .build();
        }
        String randomUrlString;
        do{
             randomUrlString = stringGenerator.generateString();
        }while(urlRepository.existsUrlByShortenedUrlStringAndIsDeletedIsFalse(randomUrlString));
        Url url = new Url();
        url.setLongUrl(urlDto.getLongUrl());
        if (urlDto.getCustomUrlString() != null) {
            url.setShortenedUrlString(urlDto.getCustomUrlString());
        }else{
            url.setShortenedUrlString(randomUrlString);
        }
        urlRepository.save(url);
        return ResponseDto.<Url>builder()
                .message(String.format("ShortUrl with id %d created", url.getId()))
                        .response(url)
                        .build();
    }
}
