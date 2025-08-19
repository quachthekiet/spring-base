package com.quachthekiet.base.security.jwt;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quachthekiet.base.model.RestResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Xử lý khi người dùng không được phép truy cập tài nguyên
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // --- LOG EXCEPTION RA CONSOLE ---
        System.out.println("=== JWT AuthenticationException ===");
        System.out.println("Exception: " + authException);
        Throwable cause = authException.getCause();
        if (cause != null) {
            System.out.println("Exception: " + cause.getClass().getName());
        }

        System.out.println("===================================");

        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        restResponse.setMessage(authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(restResponse));
    }
}
