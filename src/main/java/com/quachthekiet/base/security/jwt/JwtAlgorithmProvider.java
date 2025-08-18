package com.quachthekiet.base.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class JwtAlgorithmProvider {
    @Value("${jwt.algorithm}")
    private String jwtAlgorithm;

    public MacAlgorithm getMacAlgorithm() {
        return switch (jwtAlgorithm.toUpperCase()) {
            case "HS256" -> MacAlgorithm.HS256;
            case "HS384" -> MacAlgorithm.HS384;
            case "HS512" -> MacAlgorithm.HS512;
            default -> throw new IllegalArgumentException("Unsupported algorithm: " + jwtAlgorithm);
        };
    }
}
