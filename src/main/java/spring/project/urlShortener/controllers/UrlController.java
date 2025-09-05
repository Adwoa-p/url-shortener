package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.RandomUrlRequest;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.CustomUrlRequest;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.services.UrlService;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    private final UrlService urlService;

    @Operation(summary = "Create a new shortened URL with a randomly generated string")
    @PostMapping("/random")
    public ResponseEntity<ResponseDto<Url>> createUrl(@RequestBody final RandomUrlRequest randomUrlRequest) {
        return new ResponseEntity<>(urlService.createUrl(randomUrlRequest), HttpStatus.CREATED);
    }

    @PostMapping("/custom")
    @Operation(summary = "Create a new shortened URL with a user-defined custom string")
    public ResponseEntity<ResponseDto<Url>> createCustomUrl(@RequestBody final CustomUrlRequest customUrlRequest) {
        return new ResponseEntity<>(urlService.createCustomUrl(customUrlRequest), HttpStatus.CREATED);
    }


    @GetMapping("/r/{short-url}")
    @Operation(summary = "Redirect to the original long URL")
    public ResponseEntity<ResponseDto<Url>> redirect (@PathVariable("short-url") String shortUrlString) {
        return new ResponseEntity<>(urlService.redirect(shortUrlString), HttpStatus.FOUND);
    }

    @GetMapping()
    @Operation(summary = "Retrieve all URLS for current user")
    public ResponseEntity<Page<Url>> getAllMyUrls(@RequestParam (value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                  @RequestParam (value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                  @RequestParam (defaultValue = "shortenedUrlString",  required = false) String sortBy,
                                                  @RequestParam (defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(urlService.getAllMyUrls(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve URL details for authenticated user by ID")
    public ResponseEntity<ResponseDto<Url>> getUrlById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(urlService.getUrlById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update the details of an existing URL entry")
    public ResponseEntity<ResponseDto<String>> updateUrl(@PathVariable("id") Long id, @RequestBody final CustomUrlRequest customUrlRequest) {
        return new ResponseEntity<>(urlService.updateUrl(id, customUrlRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing URL entry by its ID")
    public ResponseEntity<ResponseDto<String>> deleteUrl(@PathVariable("id") Long id) {
        return new ResponseEntity<>(urlService.deleteUrl(id), HttpStatus.OK);
    }

}
