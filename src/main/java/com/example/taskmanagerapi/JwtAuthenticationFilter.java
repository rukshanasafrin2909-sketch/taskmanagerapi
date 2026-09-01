package com.example.taskmanagerapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Get Authorization header
            String authorizationHeader = request.getHeader("Authorization");

            // Check if header exists and starts with "Bearer "
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Extract token (remove "Bearer " prefix)
                String token = authorizationHeader.substring(7);

                // Validate token
                if (jwtUtil.validateToken(token)) {
                    // Get username from token
                    String username = jwtUtil.getUsernameFromToken(token);

                    // Store username in request attribute for later use
                    request.setAttribute("username", username);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        // Continue with the request
        filterChain.doFilter(request, response);
    }
}