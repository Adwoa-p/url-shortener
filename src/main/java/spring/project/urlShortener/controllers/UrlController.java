package spring.project.urlShortener.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.services.UrlService;

@RestController
@AllArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/create-url")
    public String createUrl(@RequestBody final Url url) {

    }

    @GetMapping("{short-url}")
    public String redirect(@PathVariable("short-url") String shortUrl) {

    }
}
