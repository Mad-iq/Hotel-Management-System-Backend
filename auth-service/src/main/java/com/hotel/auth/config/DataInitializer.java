package com.hotel.auth.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        List<String> roles = List.of(
                "ROLE_ADMIN",
                "ROLE_MANAGER",
                "ROLE_RECEPTIONIST",
                "ROLE_GUEST"
        );

        for (String roleName : roles) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(
                            new Role(null, roleName)
                    ));
        }

        System.out.println("Default roles initialized");
        
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_ADMIN")));

        if (!adminExists) {

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hms.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.getRoles().add(adminRole);

            userRepository.save(admin);

            System.out.println("✅ Default ADMIN user created");
        }

    }
}
