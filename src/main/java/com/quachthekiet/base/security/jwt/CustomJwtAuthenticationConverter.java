package com.quachthekiet.base.security.jwt;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserDetailsService userDetailsService;

    public CustomJwtAuthenticationConverter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {

        String email = Objects.requireNonNull(jwt.getSubject(), "JWT subject (email) must not be null");

        UserDetails userDetails = Objects.requireNonNull(
                userDetailsService.loadUserByUsername(email),
                "UserDetails must not be null");

        return userDetails.getAuthorities().stream()
                .map(authority -> (GrantedAuthority) () -> authority.getAuthority())
                .collect(Collectors.toList());
    }
}
