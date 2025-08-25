package com.quachthekiet.base.service.Impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public Authentication authenticateUser(String email, String password) {

        UserDetails user = userDetailsService.loadUserByUsername(email);
        System.out.println("PasswordHash: " + passwordEncoder.encode(password));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities()));

        return authentication;
    }

}
