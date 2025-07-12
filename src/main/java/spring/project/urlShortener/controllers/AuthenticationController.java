package spring.project.urlShortener.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.urlShortener.models.dtos.AuthenticationRequest;
import spring.project.urlShortener.models.dtos.AuthenticationResponse;
import spring.project.urlShortener.models.dtos.RegistrationRequest;
import spring.project.urlShortener.services.AuthenticationService;

// this is where we handle login, signup and getting JWT tokens
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Allow users to sign up")
    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signup(@RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(authenticationService.signup(registrationRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Allow users to sign in")
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> signin(@RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.signin(authenticationRequest), HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test() {
        return "Controller is working";
    }

}
