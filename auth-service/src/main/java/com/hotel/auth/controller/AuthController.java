package com.hotel.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.auth.dto.LoginRequest;
import com.hotel.auth.dto.LoginResponse;
import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.dto.RegisterResponse;
import com.hotel.auth.security.JwtUtil;
import com.hotel.auth.service.AuthService;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil; 

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

        authService.registerGuest(request);
        return new RegisterResponse("Guest registered successfully");
    }
    
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @GetMapping("/validate")
    public Claims validateToken( @RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        return jwtUtil.validateToken(token);
    }
}
