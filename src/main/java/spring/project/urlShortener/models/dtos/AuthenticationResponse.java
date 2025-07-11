package spring.project.urlShortener.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthenticationResponse  {
    private final String message;
    private final String token;
}
