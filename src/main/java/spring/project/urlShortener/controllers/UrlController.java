package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/v1")
public class UrlController {

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    private final UrlService urlService;

    @Operation(summary = "Create a new shortened URL with a randomly generated string")
    @PostMapping("/urls")
    public ResponseEntity<ResponseDto<Url>> createUrl(@RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.createUrl(urlDto), HttpStatus.CREATED);
    }

    @PostMapping("/urls/custom")
    @Operation(summary = "Create a new shortened URL with a user-defined custom string")
    public ResponseEntity<ResponseDto<Url>> createCustomUrl(@RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.createCustomUrl(urlDto), HttpStatus.CREATED);
    }


    @GetMapping("/r/{short-url}")
    @Operation(summary = "Redirect to the original long URL")
    public ResponseEntity<ResponseDto<Url>> redirect (@PathVariable("short-url") String shortUrlString) {
        return new ResponseEntity<>(urlService.redirect(shortUrlString), HttpStatus.FOUND);
    }

    @GetMapping("/urls")
    @Operation(summary = "Retrieve a list of all stored URL entries")
    public ResponseEntity<Page<Url>> getAllUrls(@RequestParam (value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                @RequestParam (value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                @RequestParam (defaultValue = "shortenedUrlString",  required = false) String sortBy,
                                                @RequestParam (defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(urlService.getAllUrls(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }

    @GetMapping("urls/{id}")
    @Operation(summary = "Retrieve URL details by ID")
    public ResponseEntity<ResponseDto<Url>> getUrlById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(urlService.getUrl(id), HttpStatus.OK);
    }

    @PutMapping("/urls/{id}")
    @Operation(summary = "Update the details of an existing URL entry")
    public ResponseEntity<ResponseDto<String>> updateUrl(@PathVariable("id") Long id, @RequestBody final UrlDto urlDto) {
        return new ResponseEntity<>(urlService.updateUrl(id,urlDto), HttpStatus.OK);
    }

    @DeleteMapping("/urls/{id}")
    @Operation(summary = "Delete an existing URL entry by its ID")
    public ResponseEntity<ResponseDto<String>> deleteUrl(@PathVariable("id") Long id) {
        return new ResponseEntity<>(urlService.deleteUrl(id), HttpStatus.OK);
    }
}
