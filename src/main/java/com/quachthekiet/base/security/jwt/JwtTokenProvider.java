package com.quachthekiet.base.security.jwt;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.quachthekiet.base.exception.InvalidTokenException;
import com.quachthekiet.base.security.model.CustomUserDetails;

@Component
public class JwtTokenProvider {
    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long jwtAccessTokenValidityInS;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshTokenExpirationIn;

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
        Instant validity = now.plusSeconds(jwtAccessTokenValidityInS);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Set<String> authorities = customUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .claim("authorities", authorities)
                .subject(customUserDetails.getUsername())
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(jwtAlgorithmProvider.getMacAlgorithm()).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        Instant validity = now.plusSeconds(jwtRefreshTokenExpirationIn);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(jwtAlgorithmProvider.getMacAlgorithm()).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String extractEmail(String token) {
        try {
            return this.jwtDecoder.decode(token).getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException("Token không hợp lệ hoặc đã hết hạn", e);
        }
    }
}
