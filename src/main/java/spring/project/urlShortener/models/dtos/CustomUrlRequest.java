package spring.project.urlShortener.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomUrlRequest {
    private String longUrl;
    private String customUrlString;

}
