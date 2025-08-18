package com.quachthekiet.base.security.jwt;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.quachthekiet.base.security.model.CustomUserDetails;

@Component
public class JwtTokenProvider {
    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Value("${jwt.token-expiration-in-seconds}")
    private long jwtExpirationInS;

    @Value("${jwt.algorithm}")
    private String jwtAlgorithm;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtAlgorithmProvider jwtAlgorithmProvider;

    public JwtTokenProvider(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtAlgorithmProvider jwtAlgorithmProvider) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtAlgorithmProvider = jwtAlgorithmProvider;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant validity = now.plusSeconds(jwtExpirationInS);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .claim("roles", customUserDetails.getAuthorities())
                .subject(customUserDetails.getUsername())
                .id(UUID.randomUUID().toString())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(jwtAlgorithmProvider.getMacAlgorithm()).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String getUsernameFromToken(String token) {
        return this.jwtDecoder.decode(token).getSubject();
    }
}
