package com.example.sims.config;

import com.example.sims.filter.JwtRequestFilter;
import com.example.sims.service.UserService;
import com.example.sims.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    
    @Bean
    public JwtRequestFilter jwtRequestFilter(UserService userService, JwtUtil jwtUtil) {
        return new JwtRequestFilter(userService, jwtUtil);
    }
}