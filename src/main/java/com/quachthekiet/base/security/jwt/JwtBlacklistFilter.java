package com.quachthekiet.base.security.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.quachthekiet.base.exception.TokenRevokedException;
import com.quachthekiet.base.service.Impl.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtBlacklistFilter extends OncePerRequestFilter {
    private final RedisService redisService;
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtBlacklistFilter(RedisService redisService, HandlerExceptionResolver handlerExceptionResolver) {
        this.redisService = redisService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String jti = jwt.getId();

                if (jti != null && redisService.isTokenBlacklisted(jti)) {
                    throw new TokenRevokedException("Token has been revoked");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }
        filterChain.doFilter(request, response);

    }

}
