package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.dtos.UserResponseDto;
import spring.project.urlShortener.models.entities.Url;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.models.enums.UserRole;
import spring.project.urlShortener.services.AdminService;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping()
    @Operation(summary = "Returns all users in the DB")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                                  @RequestParam(defaultValue = "email", required = false) String sortBy,
                                                  @RequestParam(defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(adminService.getAllUsers(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns a based on id")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.getUser(id), HttpStatus.FOUND);
    }

    // partially update user details - 1. locked 2.enable 3.update specific field(role)
    @Operation(summary = "Update User's Details - Admin")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> partialUpdateUser(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(required = false) Boolean enabled) {
        return new ResponseEntity<>(adminService.partialUpdateUser(id, locked, enabled), HttpStatus.OK);
    }

    @GetMapping("/urls")
    @Operation(summary = "Retrieve a list of all stored URL entries")
    public ResponseEntity<Page<Url>> getAllUrls(@RequestParam (value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                @RequestParam (value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                @RequestParam (defaultValue = "shortenedUrlString",  required = false) String sortBy,
                                                @RequestParam (defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(adminService.getAllUrls(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }

    @GetMapping("/urls/{id}")
    @Operation(summary = "Retrieve URL details by ID")
    public ResponseEntity<ResponseDto<Url>> getUrl(@PathVariable("id") Long id) {
        return new ResponseEntity<>(adminService.getUrl(id), HttpStatus.OK);
    }
}