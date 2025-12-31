package com.hotel.gateway.security;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final WebClient webClient;

    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return webClient.get()
                .uri("lb://auth-service/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {

                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) response.get("roles");

                    if (!isAuthorized(path, roles)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }

                    return chain.filter(exchange);
                })
                .onErrorResume(
                	    org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized.class,
                	    ex -> {
                	        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                	        return exchange.getResponse().setComplete();
                	    });

    }

    private boolean isAuthorized(String path, List<String> roles) {

        if (path.startsWith("/api/admin")) {
            return roles.contains("ROLE_ADMIN");
        }

        if (path.startsWith("/api/hotels")) {
            return roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MANAGER");
        }

        if (path.startsWith("/api/reservations")) {
            return roles.contains("ROLE_ADMIN")
                    || roles.contains("ROLE_MANAGER")
                    || roles.contains("ROLE_RECEPTIONIST")
                    || roles.contains("ROLE_GUEST");
        }

        if (path.startsWith("/api/billing")) {
            return roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MANAGER");
        }

        return true;
    }
}
