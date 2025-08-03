package spring.project.urlShortener.models;

import spring.project.urlShortener.models.dtos.UserDto;
import spring.project.urlShortener.models.entities.User;

public class UserMapper {
    public static UserDto toDTo(User user) {
        return new UserDto(
                user.getEmail(),
                user.getFirstName() + "" + user.getLastName(),
                user.getLocked(),
                user.getEnabled(),
                user.getRole()
        );
    }
}
