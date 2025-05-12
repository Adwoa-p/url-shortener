package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
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

    @Operation(summary = "Create a new url with a random string")
    @PostMapping("/create-url")
    public ResponseEntity<ResponseDto<Url>> createUrl(@RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.createUrl(urlDto), HttpStatus.CREATED);
    }

    @PostMapping("/create-custom-url")
    @Operation(summary = "Create a new url with a custom string")
    public ResponseEntity<ResponseDto<Url>> createCustomUrl(@RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.createCustomUrl(urlDto), HttpStatus.CREATED);
    }
}
