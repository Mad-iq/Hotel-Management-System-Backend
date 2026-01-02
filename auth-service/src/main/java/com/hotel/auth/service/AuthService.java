package com.hotel.auth.service;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hotel.auth.dto.CreateStaffUserRequest;
import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.LoginResponse;
import com.hotel.auth.dto.ProfileResponse;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.entity.Role;
import com.hotel.auth.entity.User;
import com.hotel.auth.exception.UserAlreadyExistsException;
import com.hotel.auth.repository.RoleRepository;
import com.hotel.auth.repository.UserRepository;
import com.hotel.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;

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
    
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        var roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();

        String token = jwtUtil.generateToken(user.getId(),user.getUsername(), roles);

        return new LoginResponse(
                token,
                "Bearer",
                86400,
                roles
        );
    }
    
    public void createStaffUser(CreateStaffUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (!request.getRole().equals("ROLE_MANAGER") && !request.getRole().equals("ROLE_RECEPTIONIST")) {
            throw new IllegalArgumentException("Only MANAGER or RECEPTIONIST can be created");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(role));
        user.setHotelIds(request.getHotelIds());

        userRepository.save(user);
    }
    
    
    public ProfileResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated");
        }

        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(java.util.stream.Collectors.toSet())
        );

        return response;
    }


}
