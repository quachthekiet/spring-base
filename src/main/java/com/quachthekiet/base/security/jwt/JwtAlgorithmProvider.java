package com.quachthekiet.base.security.jwt;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class JwtAlgorithmProvider {
    public MacAlgorithm getMacAlgorithm() {
        return MacAlgorithm.HS512; // You can add more algorithms and return based on jwtAlgorithm value
    }
}
