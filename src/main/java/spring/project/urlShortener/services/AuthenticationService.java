package spring.project.urlShortener.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.project.urlShortener.models.dtos.*;
import spring.project.urlShortener.models.entities.Role;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.repository.RoleRepository;
import spring.project.urlShortener.repository.UserRepository;
import spring.project.urlShortener.security.JwtService;

import java.util.Set;


@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService service;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationService(UserRepository userRepository, JwtService service, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }

    public RegistrationResponse signup(RegistrationRequest registrationRequest) {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        var user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(Set.of(userRole))
                .enabled(true)
                .build();
        userRepository.save(user);
        return RegistrationResponse.builder()
                .message(String.format("User with name %s %s created successfully", registrationRequest.getFirstName(), registrationRequest.getLastName()))
                .build();
    }

    public AuthenticationResponse signin(AuthenticationRequest authenticationRequest) {
        System.out.println("Starting Authentication");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername()
                        , authenticationRequest.getPassword()
                )
        );
        System.out.println("User Authenticated: " + authenticationRequest.getUsername());

        var user = userRepository.findByEmail(authenticationRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("User Found: " + authenticationRequest.getUsername());

        var token = service.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .message("User has signed in successfully")
                .build();
    }

}
