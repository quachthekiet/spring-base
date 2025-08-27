package com.quachthekiet.base.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quachthekiet.base.model.AuthRequest;
import com.quachthekiet.base.model.AuthResponse;
import com.quachthekiet.base.model.RestResponse;
import com.quachthekiet.base.security.jwt.JwtTokenProvider;
import com.quachthekiet.base.service.Impl.AuthService;
import com.quachthekiet.base.service.Impl.RedisService;
import com.quachthekiet.base.service.Impl.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider,
            RedisService redisService, JwtDecoder jwtDecoder,
            UserService userService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<?>> login(@RequestBody @Valid AuthRequest authRequest) {

        Authentication authentication = authService.authenticateUser(authRequest.getEmail(),
                authRequest.getPassword());

        String token = jwtTokenProvider.generateToken(authentication);

        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());
        var user = userService.getUserByEmail(authentication.getName());
        user.setRefreshToken(refreshToken);
        userService.updateUser(user);

        RestResponse<AuthResponse> response = new RestResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Login success");
        response.setData(AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("refresh-token")
    public String postMethodName(@RequestHeader("Authorization") String refreshHeader) {
        return authService.refreshAccessToken(refreshHeader);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        if (token != null && !token.isBlank()) {
            // Nếu client gửi token kèm "Bearer " thì cắt bỏ
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            try {
                Jwt jwt = jwtDecoder.decode(token);
                String jti = jwt.getId();
                var expiresAt = jwt.getExpiresAt();
                if (expiresAt != null) {
                    long ttl = expiresAt.getEpochSecond() - System.currentTimeMillis() / 1000;
                    redisService.addToBlacklist(jti, ttl);
                }

            } catch (Exception e) {
                // token invalid hoặc expired -> ignore
            }
        }
        return "Success";
    }

}
