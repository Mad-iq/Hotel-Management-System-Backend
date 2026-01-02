package com.hotel.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager authManager
    ) {

        AuthenticationWebFilter jwtFilter =new AuthenticationWebFilter(authManager);
        jwtFilter.setServerAuthenticationConverter(bearerTokenConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchanges -> exchanges

                	    .pathMatchers("/api/auth/**").permitAll()

                	    .pathMatchers(HttpMethod.POST, "/api/user", "/api/user/")
                	        .hasRole("ADMIN")

                	   
                	    .pathMatchers(HttpMethod.POST, "/api/hotels", "/api/hotels/")
                	        .hasRole("ADMIN")

                	    .pathMatchers(HttpMethod.GET, "/api/hotels", "/api/hotels/")
                	        .permitAll()

                	    .pathMatchers(HttpMethod.POST, "/api/hotels/*/categories")
                	        .hasRole("ADMIN")

                	    .pathMatchers(HttpMethod.GET, "/api/hotels/*/categories")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	 
                	    .pathMatchers(HttpMethod.POST, "/api/hotels/*/rooms")
                	        .hasRole("ADMIN")

                	    .pathMatchers(HttpMethod.GET, "/api/hotels/*/rooms")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    .pathMatchers(HttpMethod.PUT, "/api/hotels/*/rooms/*/status")
                	        .hasAnyRole("MANAGER", "RECEPTIONIST" )

                	    .pathMatchers(HttpMethod.POST, "/api/categories/*/pricing")
                	        .hasRole("ADMIN")

                	    .pathMatchers(HttpMethod.GET, "/api/categories/*/pricing")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    .pathMatchers(HttpMethod.POST, "/api/categories/*/seasonal-pricing")
                	        .hasAnyRole("ADMIN","MANAGER" )

                	    .pathMatchers(HttpMethod.GET, "/api/categories/*/seasonal-pricing")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    //booking
//                	    .pathMatchers("/api/bookings/**")
//                	        .hasAnyRole("GUEST", "ADMIN")
                	        
                	    .pathMatchers(HttpMethod.POST, "/api/bookings/*/check-in")
                	        .hasAnyRole("RECEPTIONIST", "MANAGER")

                	    .pathMatchers(HttpMethod.POST, "/api/bookings/*/check-out")
                	        .hasAnyRole("RECEPTIONIST", "MANAGER")
                	        
                	    .pathMatchers(HttpMethod.POST, "/api/bookings")
                	        .hasRole("GUEST")

                	    .pathMatchers(HttpMethod.DELETE, "/api/bookings/*")
                	        .hasRole("GUEST")

                	    .pathMatchers(HttpMethod.GET, "/api/bookings/user/*")
                	        .hasRole("GUEST")

                	    .pathMatchers(HttpMethod.POST, "/api/bookings/search/hotels")
                	        .permitAll()
                	        
                	    .pathMatchers(HttpMethod.GET, "/api/bookings")
                	        .hasRole("ADMIN")

                        //others
                	     .pathMatchers("/api/payments/**")
                	        .hasRole("GUEST")
                	        
                	     .pathMatchers("/api/reports/**")
                	        .hasAnyRole("ADMIN", "MANAGER")
                	        
//                	    .pathMatchers("/api/billing/**")
//                	        .hasAnyRole("ADMIN", "MANAGER")
//
//                	    .pathMatchers("/api/reservations/**")
//                	        .hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST", "GUEST")

                	    //fallback
                	    .anyExchange().authenticated()
                	)


                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            org.springframework.web.reactive.function.client.WebClient.Builder builder
    ) {
        return new JwtReactiveAuthenticationManager(builder);
    }

    private ServerAuthenticationConverter bearerTokenConverter() {
        return exchange -> {
            String header =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                return Mono.empty();
            }

            String token = header.substring(7);

            return Mono.just(
                    new org.springframework.security.authentication
                            .UsernamePasswordAuthenticationToken(null, token)
            );
        };
    }
}
