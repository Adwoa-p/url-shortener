package spring.project.urlShortener.models.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spring.project.urlShortener.models.enums.UserRole;

@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private String username;
    private String fullName;
    private Boolean locked;
    private Boolean enabled;
}
