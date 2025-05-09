package spring.project.urlShortener.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UrlDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.services.UrlService;

@RestController
public class UrlController {

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    private final UrlService urlService;

    @PostMapping("/create-url")
    public ResponseEntity<ResponseDto<Url>> createUrl(@RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.createUrl(urlDto), HttpStatus.CREATED);
    }

    @GetMapping("{short-url}")
    public String redirect(@PathVariable("short-url") String shortUrl) {
        return null;
    }
}
