package com.hotel.auth.controller;

import com.hotel.auth.dto.CreateStaffUserRequest;
import com.hotel.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserManagementController {

    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createStaffUser(@Valid @RequestBody CreateStaffUserRequest request) {
        authService.createStaffUser(request);
    }
}
