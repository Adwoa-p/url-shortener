package spring.project.urlShortener.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
