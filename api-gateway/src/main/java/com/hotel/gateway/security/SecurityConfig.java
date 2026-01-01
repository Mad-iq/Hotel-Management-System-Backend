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

        AuthenticationWebFilter jwtFilter =
                new AuthenticationWebFilter(authManager);

        jwtFilter.setServerAuthenticationConverter(bearerTokenConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchanges -> exchanges

                	    // ---------- AUTH ----------
                	    .pathMatchers("/api/auth/**").permitAll()

                	    // ---------- USER MANAGEMENT ----------
                	    .pathMatchers(HttpMethod.POST, "/api/user", "/api/user/")
                	        .hasRole("ADMIN")

                	    // ---------- HOTELS ----------

                	    // 1️⃣ Create Hotel (ADMIN)
                	    .pathMatchers(HttpMethod.POST, "/api/hotels", "/api/hotels/")
                	        .hasRole("ADMIN")

                	    // 2️⃣ List Hotels (PUBLIC)
                	    .pathMatchers(HttpMethod.GET, "/api/hotels", "/api/hotels/")
                	        .permitAll()

                	    // ---------- ROOM CATEGORIES ----------

                	    // 3️⃣ Create Room Category (ADMIN)
                	    .pathMatchers(HttpMethod.POST, "/api/hotels/*/categories")
                	        .hasRole("ADMIN")

                	    // 4️⃣ List Room Categories (ADMIN / MANAGER)
                	    .pathMatchers(HttpMethod.GET, "/api/hotels/*/categories")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    // ---------- ROOMS / INVENTORY ----------

                	    // 5️⃣ Add Room to Inventory (ADMIN)
                	    .pathMatchers(HttpMethod.POST, "/api/hotels/*/rooms")
                	        .hasRole("ADMIN")

                	    // 6️⃣ List Rooms in Hotel (ADMIN / MANAGER)
                	    .pathMatchers(HttpMethod.GET, "/api/hotels/*/rooms")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    // 7️⃣ Update Room Status (MANAGER)
                	    .pathMatchers(HttpMethod.PUT, "/api/hotels/*/rooms/*/status")
                	        .hasRole("MANAGER")

                	    // ---------- PRICING ----------

                	    // 8️⃣ Set Base Pricing (ADMIN)
                	    .pathMatchers(HttpMethod.POST, "/api/categories/*/pricing")
                	        .hasRole("ADMIN")

                	    // 9️⃣ Get Base Pricing (ADMIN / MANAGER)
                	    .pathMatchers(HttpMethod.GET, "/api/categories/*/pricing")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    // 🔟 Add Seasonal Pricing (ADMIN)
                	    .pathMatchers(HttpMethod.POST, "/api/categories/*/seasonal-pricing")
                	        .hasRole("ADMIN")

                	    // 1️⃣1️⃣ Get Seasonal Pricing (ADMIN / MANAGER)
                	    .pathMatchers(HttpMethod.GET, "/api/categories/*/seasonal-pricing")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    // ---------- OTHER SERVICES ----------
                	    .pathMatchers("/api/bookings/**")
                	        .hasAnyRole("GUEST", "USER", "ADMIN")
                	        
                	    .pathMatchers("/api/billing/**")
                	        .hasAnyRole("ADMIN", "MANAGER")

                	    .pathMatchers("/api/reservations/**")
                	        .hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST", "GUEST")

                	    // ---------- FALLBACK ----------
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
