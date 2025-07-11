package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.services.UserService;

@RestController
@RequestMapping("api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(summary = "Returns all users in the DB")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                    @RequestParam (value="pageSize", defaultValue = "10", required = false) int pageSize,
                                                    @RequestParam (defaultValue = "email",  required = false) String sortBy,
                                                    @RequestParam (defaultValue = "true") boolean ascending) {
        return new ResponseEntity<>(userService.getAllUsers(pageNo, pageSize, sortBy, ascending), HttpStatus.OK);
    }
}
