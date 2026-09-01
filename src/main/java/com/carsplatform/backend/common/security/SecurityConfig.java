package com.carsplatform.backend.common.security;

import com.carsplatform.backend.common.security.jwt.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;


/**
 * Security setup: no sessions, a JWT on every request, BCrypt for passwords and CORS limited
 * to the origins listed in {@code app.cors.allowed-origins}.
 *
 * Browsing the catalogue and the authentication endpoints are open to everyone, everything
 * under /api/admin needs the ADMIN role and the rest needs a logged-in user. Requests that
 * fail those rules are answered by the two handlers below with the same JSON shape the
 * controllers use, instead of the default Spring Security page.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final List<String> allowedOrigins;


    public SecurityConfig(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            @Lazy UserDetailsService userDetailsService,
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.allowedOrigins = allowedOrigins;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        // Not logged in
                        .authenticationEntryPoint((request, response, authException) ->
                                SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Authentication is required to access this resource."))
                        // Logged in, but without the required role
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN,
                                        "You do not have permission to perform this action."))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/webjars/**",
                                "/actuator/health"
                        ).permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/brands/**", "/api/models/**", "/api/generations/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/body-types/**", "/api/tags/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**", "/api/fuel-reports/**").permitAll()

                        .requestMatchers("/api/users/**", "/api/user-settings/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**", "/api/fuel-reports/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**", "/api/fuel-reports/**").authenticated()
                        .requestMatchers("/api/data-proposals/**").authenticated()
                        .requestMatchers("/api/likes/**").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
