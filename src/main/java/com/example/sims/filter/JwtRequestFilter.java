package com.example.sims.filter;

import com.example.sims.service.UserService;
import com.example.sims.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import java.util.*;

import java.util.stream.Collectors;

public class JwtRequestFilter extends OncePerRequestFilter {
   
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        final String authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwt = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        Claims claims = jwtUtil.getClaimsFromToken(jwt);
                        
                        // 1. Ensure roles exist in token
                        if(claims.get("roles") == null){
                            throw new RuntimeException("No roles present in token");
                        }

                        // 2. Properly extract roles with Role_prefix
                        Set<String> tokenRoles = ((List<?>) claims.get("roles")).stream()
                        .map(Object::toString)
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .collect(Collectors.toSet());

                       // 3. Get authorities from UserDetails
                       Set<String> userAuthorities = userDetails.getAuthorities().stream()
                       .map(GrantedAuthority::getAuthority)
                       .collect(Collectors.toSet());

                   // 4. Compare roles (case-sensitive exact match)
                   if (!tokenRoles.equals(userAuthorities)) {
                       logger.error("Role mismatch - Token: {} vs DB: {}", tokenRoles, userAuthorities);
                       throw new RuntimeException("Token roles don't match user roles");
                   }
                   
                    // 5. Create authentication token
                    Collection<? extends GrantedAuthority> authorities = tokenRoles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
    
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                authorities);
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("JWT Processing Error", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization failded: " + e.getMessage());
            return;
        }
        
        chain.doFilter(request, response);
    }
}