package spring.project.urlShortener.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.project.urlShortener.exceptions.ResourceNotFoundException;
import spring.project.urlShortener.models.entities.Role;
import spring.project.urlShortener.models.entities.User;
import spring.project.urlShortener.repository.RoleRepository;
import spring.project.urlShortener.repository.UserRepository;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepository userRepository;

    // for hashing the password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // this connects the user model with Spring Security's login system.
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> (UserDetails) userRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("Username not found"));
    }

    // used to validate username and password during login.
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    // performs authentication in code like in a login service. It delegates to the provider you defined.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, @Value("${ADMIN_PASSWORD}") String password)  {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRoles(Set.of(adminRole));
                userRepository.save(admin);
                System.out.println("Admin: " + admin.getEmail() + " created successfully");
            }
        };
    }

}
