package spring.project.urlShortener.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.UserMapper;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UserDto;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public Page<User> getAllUsers(int pageNo, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return userRepository.findAll(pageable);
    }

    public ResponseDto<UserDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDto userDto = UserMapper.toDTo(user);
        return ResponseDto.<UserDto>builder()
                .message(String.format("Found user with id %d", id))
                .response(userDto)
                .build();
    }

    public ResponseDto<UserDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth==null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }
        String username = auth.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not Found"));
        UserDto userDto = UserMapper.toDTo(user);
        return ResponseDto.<UserDto>builder()
                .message("Returning Authenticated User")
                .response(userDto)
                .build();
    }

    // update user details
    // delete user
}
