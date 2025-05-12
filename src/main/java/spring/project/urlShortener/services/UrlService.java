package spring.project.urlShortener.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import spring.project.urlShortener.config.StringGenerator;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.repository.UrlRepository;


@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final StringGenerator stringGenerator;

    @Autowired
    public UrlService(UrlRepository urlRepository, StringGenerator stringGenerator) {
        this.urlRepository = urlRepository;
        this.stringGenerator = stringGenerator;
    }

    public ResponseDto<Url> createUrl(UrlDto urlDto) {
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
        return new RedirectView(longUrl.getLongUrl());
    }
}
