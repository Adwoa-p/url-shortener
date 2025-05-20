package spring.project.urlShortener.models.entities;

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

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private Boolean isExpired;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusDays(90);
    }

}
