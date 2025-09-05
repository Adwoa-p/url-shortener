package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.PasswordRequest;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UserRequestDto;
import spring.project.urlShortener.models.dtos.UserResponseDto;
import spring.project.urlShortener.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // get the current authenticated user
    @GetMapping("/me")
    @Operation(summary = "Returns current authenticated user")
    public ResponseEntity<ResponseDto<UserResponseDto>> getCurrentUser() {
        return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.FOUND);
    }

    // update user details fully
    @PutMapping("/{id}")
    @Operation(summary = "Update current user details")
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
        return new ResponseEntity<>(userService.updateUser(id, userRequestDto), HttpStatus.OK);
    }

    // patch - reset user password
    @PatchMapping("{id}/reset")
    @Operation(summary = "Update User's password")
    public ResponseEntity<ResponseDto<String>> updateUserPassword(@PathVariable Long id, @RequestBody PasswordRequest passwordRequest) {
        return new ResponseEntity<>(userService.updatePassword(id, passwordRequest), HttpStatus.OK);
    }

    // delete user
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ResponseDto<String>> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}