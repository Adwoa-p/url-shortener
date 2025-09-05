package spring.project.urlShortener.models.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordRequest {
    private String newPassword;
}
