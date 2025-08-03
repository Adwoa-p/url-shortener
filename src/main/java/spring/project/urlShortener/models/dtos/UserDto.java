package spring.project.urlShortener.models.dtos;
import lombok.AllArgsConstructor;
import spring.project.urlShortener.models.enums.UserRole;

@AllArgsConstructor
public class UserDto {
    private String username;
    private String fullName;
    private Boolean locked;
    private Boolean enabled;
    private UserRole role;

}
