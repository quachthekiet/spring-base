package com.quachthekiet.base.model;

import org.springframework.http.ResponseCookie;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    ResponseCookie accessTokenCookie;
    ResponseCookie refreshTokenCookie;
}
