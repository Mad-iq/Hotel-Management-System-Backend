package com.hotel.auth.controller;

import com.hotel.auth.dto.RegisterRequest;
import com.hotel.auth.dto.RegisterResponse;
import com.hotel.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

        authService.registerGuest(request);
        return new RegisterResponse("Guest registered successfully");
    }
}
