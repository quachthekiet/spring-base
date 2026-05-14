package com.quachthekiet.base.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quachthekiet.base.constant.TokenCookieConstants;
import com.quachthekiet.base.model.AuthRequest;
import com.quachthekiet.base.model.AuthResponse;
import com.quachthekiet.base.common.RestResponse;
import com.quachthekiet.base.security.jwt.JwtTokenProvider;
import com.quachthekiet.base.service.AuthService;
import com.quachthekiet.base.service.UserService;
import com.quachthekiet.base.service.Impl.RedisService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<RestResponse<String>> login(@RequestBody @Valid AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest);

        return withAuthCookies(authResponse)
                .body(RestResponse.success("Đăng nhập thành công"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<String>> refreshToken(
            @CookieValue(name = TokenCookieConstants.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {

        AuthResponse authResponse = authService.refreshToken(refreshToken);

        return withAuthCookies(authResponse)
                .body(RestResponse.success("Lấy token mới thành công"));
    }

    @PostMapping("/logout")
    public ResponseEntity<RestResponse<?>> logout(
            @CookieValue(name = TokenCookieConstants.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {

        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        buildClearCookie(TokenCookieConstants.ACCESS_TOKEN_COOKIE, "/").toString())
                .header(HttpHeaders.SET_COOKIE,
                        buildClearCookie(TokenCookieConstants.REFRESH_TOKEN_COOKIE, "/api/auth/refresh").toString())
                .body(RestResponse.success("Đăng xuất thành công"));
    }

    /**
     * Tối ưu helper để trả về BodyBuilder đã được cấu hình sẵn Cookie
     */
    private ResponseEntity.BodyBuilder withAuthCookies(AuthResponse authResponse) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();

        if (authResponse.getAccessTokenCookie() != null) {
            builder.header(HttpHeaders.SET_COOKIE, authResponse.getAccessTokenCookie().toString());
        }

        if (authResponse.getRefreshTokenCookie() != null) {
            // Lưu ý: Dùng .header() lần 2 với cùng key HttpHeaders.SET_COOKIE
            // trong Spring sẽ tự động append thêm vào danh sách header thay vì ghi đè.
            builder.header(HttpHeaders.SET_COOKIE, authResponse.getRefreshTokenCookie().toString());
        }

        return builder;
    }

    private ResponseCookie buildClearCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true) // Nên luôn để true trong production
                .sameSite("Lax")
                .path(path)
                .maxAge(0) // Xóa ngay lập tức
                .build();
    }

}
