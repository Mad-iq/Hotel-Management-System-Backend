package com.hotel.gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserContextFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> injectUserId(exchange, chain, auth))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> injectUserId(ServerWebExchange exchange,
                                    org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                                    Authentication auth) {

        if (auth.getCredentials() instanceof Map<?, ?> claims) {

            Object userId = claims.get("userId");

            if (userId != null) {
                exchange = exchange.mutate()
                        .request(r -> r.header("X-USER-ID", userId.toString()))
                        .build();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
