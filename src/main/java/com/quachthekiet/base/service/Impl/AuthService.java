package com.quachthekiet.base.service.Impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.quachthekiet.base.model.User;
import com.quachthekiet.base.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public AuthService(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider, JwtDecoder jwtDecoder, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    public Authentication authenticateUser(String email, String password) {

        UserDetails user = userDetailsService.loadUserByUsername(email);
        System.out.println("PasswordHash: " + passwordEncoder.encode(password));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities()));

        return authentication;
    }

    public String refreshAccessToken(String refreshHeader) {
        if (refreshHeader == null || !refreshHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String refreshToken = refreshHeader.substring(7);

        Jwt jwt = jwtDecoder.decode(refreshToken);
        String email = jwt.getSubject();
        Instant expiresAt = jwt.getExpiresAt();

        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = userService.getUserByEmail(email);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Refresh token invalid");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities()));
    }

}
