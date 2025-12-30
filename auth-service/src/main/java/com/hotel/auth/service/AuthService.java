package com.hotel.auth.service;

import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.UserAlreadyExistsException;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerGuest(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        Role guestRole = roleRepository.findByName("ROLE_GUEST")
                .orElseThrow(() -> new RuntimeException("ROLE_GUEST not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(guestRole));

        userRepository.save(user);
    }
}
