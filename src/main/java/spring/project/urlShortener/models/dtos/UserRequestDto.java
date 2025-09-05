package spring.project.urlShortener.models.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spring.project.urlShortener.models.enums.UserRole;

@AllArgsConstructor
@Getter
@Setter
public class UserRequestDto {
    private String email;
    private String firstName;
    private String lastName;
}
