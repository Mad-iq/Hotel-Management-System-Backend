package com.hotel.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.hotel.booking.client.dto.UserProfileDto;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {

    @GetMapping("/api/auth/profile")
    UserProfileDto getProfile(@RequestHeader("Authorization") String authorization);
    
    @GetMapping("/api/auth/internal/users/{userId}")
    UserProfileDto getProfileByUserId(@PathVariable Long userId);
}
