package com.quachthekiet.base.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // Xử lý khi người dùng không có quyền truy cập tài nguyên
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":403,\"message\":\"Forbidden: " + accessDeniedException.getMessage() + "\"}");
        writer.flush();
    }

}
