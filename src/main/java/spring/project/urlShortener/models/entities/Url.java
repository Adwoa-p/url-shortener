package spring.project.urlShortener.models.entities;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Url {
    private Long id;
    private String originalUrl;
    private String shortenedUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
