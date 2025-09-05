package spring.project.urlShortener.models;

import spring.project.urlShortener.models.dtos.UserResponseDto;
import spring.project.urlShortener.models.entities.User;

public class UserMapper {
    public static UserResponseDto toDTo(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getFirstName() + "" + user.getLastName(),
                user.getLocked(),
                user.getEnabled()
        );
    }
}
