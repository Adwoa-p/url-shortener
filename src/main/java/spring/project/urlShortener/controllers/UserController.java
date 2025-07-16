package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.ResponseDto;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.services.UserService;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @Operation(summary = "Returns all users in the DB")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                    @RequestParam (value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                    @RequestParam (defaultValue = "email",  required = false) String sortBy,
                                                    @RequestParam (defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(userService.getAllUsers(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns a based on id")
    public ResponseEntity<ResponseDto<User>> getUser(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.FOUND);
    }

    // get the current authenticated user
    @GetMapping("/me")
    @Operation(summary = "Returns current authenticated user")
    public ResponseEntity<ResponseDto<User>> getCurrentUser(){
        return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.FOUND);
    }

    // update user details fully
    // partially update user details - 1. activate 2.deactivate 3.update specific field(role)
    // post - reset user password
    // delete user
}
