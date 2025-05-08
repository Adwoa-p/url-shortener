package spring.project.urlShortener.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Table(name = "urls")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Url {

    @Id
    @SequenceGenerator(
            name = "todo_sequence",
            sequenceName = "todo_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "todo_sequence"
    )
    private Long id;

    private String longUrl;
    private String shortenedUrlString;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortenedUrlString() {
        return shortenedUrlString;
    }

    public void setShortenedUrlString(String shortenedUrlString) {
        this.shortenedUrlString = shortenedUrlString;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
