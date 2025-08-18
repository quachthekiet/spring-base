package com.quachthekiet.base.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public Authentication authenticateUser(String email, String password) {

        UserDetails user = userDetailsService.loadUserByUsername(email);

        // if (!passwordEncoder.matches(password, user.getPassword())) {
        // throw new BadCredentialsException("Wrong password");
        // }

        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

}
