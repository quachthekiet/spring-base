package com.quachthekiet.base.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quachthekiet.base.model.RestResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Xử lý khi người dùng không có quyền truy cập tài nguyên
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        System.out.println("=== JWT AuthorizationException ===");
        System.out.println("AccessDeniedException class: " + accessDeniedException);
        Throwable cause = accessDeniedException.getCause();
        if (cause != null) {
            System.out.println("Cause: " + cause.getClass().getName());
        }
        System.out.println("===================================");
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(HttpStatus.FORBIDDEN.value());
        restResponse.setMessage(accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(restResponse));
    }

}
