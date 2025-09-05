package spring.project.urlShortener.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Table(name = "urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Url {

    @Id
    @SequenceGenerator(
            name = "url_sequence",
            sequenceName = "url_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "url_sequence"
    )
    private Long id;

    private String longUrl;
    private String shortenedUrlString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private Boolean isExpired;
    private Boolean isDeleted;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusDays(90);
        this.isExpired = false;
        this.isDeleted = false;
    }

}
