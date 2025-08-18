package com.quachthekiet.base.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    // Xử lý khi người dùng không được phép truy cập tài nguyên
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // --- LOG EXCEPTION RA CONSOLE ---
        System.out.println("=== JWT AuthenticationException ===");
        System.out.println("authException: " + authException);
        Throwable cause = authException.getCause();
        if (cause != null) {
            System.out.println("Cause: " + cause);
            cause.printStackTrace(System.out);
        }
        System.out.println("===================================");

        // --- TRẢ RESPONSE ---
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String message = authException.getMessage();

        if (cause instanceof BadJwtException) {
            message = "Invalid JWT signature or malformed token";
        } else if (cause instanceof JwtException) {
            message = "JWT validation failed (expired or invalid claims)";
        }

        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":401,\"message\":\"Unauthorized: " + message + "\"}");
        writer.flush();
    }
}
