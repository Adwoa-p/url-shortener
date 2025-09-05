package spring.project.urlShortener.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.UserMapper;
import spring.project.urlShortener.models.dtos.PasswordRequest;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UserRequestDto;
import spring.project.urlShortener.models.dtos.UserResponseDto;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth==null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }
        String username = auth.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not Found"));

    }

    @Override
    public UserDetails loadUserByUsername(String username){
        System.out.println("Trying to find user: " + username);;
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        // turns the role name into a permission object to allow the app know what that particular user/admin is allowed  to do
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        return Collections.singletonList(authority);
    }

    public ResponseDto<UserResponseDto> getCurrentUser() {
        User user = getAuthenticatedUser();
        UserResponseDto userResponseDto = UserMapper.toDTo(user);
        return ResponseDto.<UserResponseDto>builder()
                .message("Returning Authenticated User")
                .response(userResponseDto)
                .build();
    }

    // update user details - for users
    public ResponseDto<UserResponseDto> updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEmail(userRequestDto.getEmail());
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        userRepository.save(user);
        UserResponseDto userDto = UserMapper.toDTo(user);
        return ResponseDto.<UserResponseDto>builder()
                .message("User deleted successfully")
                .response(userDto)
                .build();
    }

    // delete user - users
    public ResponseDto<String> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsDeleted(true);
        user.setEnabled(false);
        user.setLocked(true);
        userRepository.save(user);
        return ResponseDto.<String>builder()
                .message("User deleted successfully")
                .build();
    }

    public ResponseDto<String> updatePassword(Long id, PasswordRequest passwordRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String password = encoder.encode(passwordRequest.getNewPassword());
        user.setPassword(password);
        userRepository.save(user);
        return ResponseDto.<String>builder()
                .message("User password updated successfully")
                .build();
    }
}
