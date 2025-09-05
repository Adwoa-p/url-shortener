package spring.project.urlShortener.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.UserMapper;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UserResponseDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.models.enums.UserRole;
import spring.project.urlShortener.repository.UrlRepository;
import spring.project.urlShortener.repository.UserRepository;

import java.util.Set;

@Service
public class AdminService{
    private final UserRepository userRepository;
    private final UrlRepository urlRepository;

    public AdminService(UserRepository userRepository, UrlRepository urlRepository) {
        this.userRepository = userRepository;
        this.urlRepository = urlRepository;
    }

    public Page<User> getAllUsers(int pageNo, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return userRepository.findAll(pageable);
    }

    public ResponseDto<UserResponseDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserResponseDto userResponseDto = UserMapper.toDTo(user);
        return ResponseDto.<UserResponseDto>builder()
                .message(String.format("Found user with id %d", id))
                .response(userResponseDto)
                .build();
    }


    //    for admins
    public ResponseDto<String> partialUpdateUser(Long id, Boolean locked, Boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLocked(locked);
        user.setEnabled(enabled);
        userRepository.save(user);
        return ResponseDto.<String>builder()
                .message("User updated successfully")
                .build();
    }

    public Page<Url> getAllUrls(int pageNo, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return urlRepository.findAllByIsDeletedIsFalse(pageable);
    }

    public ResponseDto<Url> getUrl(Long id) {
        Url longUrl = urlRepository.findByIdAndIsDeletedIsFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Long url with id %d not found", id)));
        return ResponseDto.<Url>builder()
                .message("Returning long url by ID")
                .response(longUrl)
                .build();
    }
}


