package spring.project.urlShortener.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record AuthenticationResponse(String message, String token) {
}
