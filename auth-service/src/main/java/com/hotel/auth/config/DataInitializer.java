package com.hotel.auth.config;

import com.hotel.auth.entity.Role;
import com.hotel.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

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
    }
}
