package com.example.sims.config;

import com.example.sims.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/inventory/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/inventory/add").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/inventory/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/inventory/{id}").hasRole("ADMIN")

                .requestMatchers("/api/suppliers/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")
                .requestMatchers("/api/activity-logs/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        
        return http.build();
    }
}