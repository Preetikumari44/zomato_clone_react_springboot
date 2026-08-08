package com.novabyte.zomatoclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import com.novabyte.zomatoclone.security.JwtAccessDeniedHandler;
import com.novabyte.zomatoclone.security.JwtAuthEntryPoint;
import com.novabyte.zomatoclone.security.JwtAuthenticationFilter;

/**
 * Endpoint-level RBAC lives here as coarse role gates (hasRole checks).
 * Ownership checks (e.g. "is this YOUR restaurant") can't be expressed as
 * a URL pattern rule and are enforced in the service layer instead — see
 * ForbiddenOperationException usages from Phase 2 onward.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserDetailsService userDetailsService;

    // Reads from the CORS_ALLOWED_ORIGINS env var set on Render, e.g.:
    // CORS_ALLOWED_ORIGINS=http://localhost:5173,https://zomato-clone-react-springboot.vercel.app
    // Comma-separated, no spaces around commas.
    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173}")
    private String corsAllowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           JwtAuthEntryPoint jwtAuthEntryPoint,
                           JwtAccessDeniedHandler jwtAccessDeniedHandler,
                           UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS configuration source. This is the piece that was missing before —
     * .cors(cors -> {}) alone does NOT allow any origin, it just enables the
     * CORS filter with no config, so the Access-Control-Allow-Origin header
     * never gets added and the browser blocks the preflight request.
     *
     * Update allowedOriginPatterns below if your Vercel domain changes.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/health", "/api/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants", "/api/restaurants/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/restaurants").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/restaurants/{id}").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/{id}/logo").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/owner/mine").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/restaurants/{id}/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/restaurants/{id}/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/{restaurantId}/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/{restaurantId}/menu").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/{restaurantId}/categories").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/{restaurantId}/menu").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/restaurants/{restaurantId}/menu/{itemId}").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/{restaurantId}/menu/{itemId}").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/{restaurantId}/menu/{itemId}/image").hasRole("RESTAURANT_OWNER")
                        .requestMatchers("/api/cart", "/api/cart/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/mine").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/restaurant/{restaurantId}").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/{id}/accept").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/{id}/reject").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/{id}/status").hasRole("RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/{id}")
                            .hasAnyRole("CUSTOMER", "RESTAURANT_OWNER", "DELIVERY_PARTNER", "ADMIN")
                        .requestMatchers("/api/delivery/**").hasRole("DELIVERY_PARTNER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
