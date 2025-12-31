package com.hotel.gateway.security;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final WebClient webClient;

    public JwtReactiveAuthenticationManager(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String token = (String) authentication.getCredentials();

        return webClient.get()
                .uri("lb://auth-service/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {

                    String username = (String) response.get("sub");

                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) response.get("roles");

                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    return new UsernamePasswordAuthenticationToken(
                            username,
                            token,
                            authorities
                    );
                });
    }
}
