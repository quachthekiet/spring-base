package com.quachthekiet.base.service.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.quachthekiet.base.constant.TokenCookieConstants;
import com.quachthekiet.base.exception.InvalidTokenException;
import com.quachthekiet.base.model.AuthRequest;
import com.quachthekiet.base.model.AuthResponse;
import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;
import com.quachthekiet.base.security.jwt.JwtTokenProvider;
import com.quachthekiet.base.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Sử dụng Log để debug thay vì System.out
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${jwt.cookie.same-site:Lax}")
    private String cookieSameSite;
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiry;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiry;

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            String accessToken = jwtTokenProvider.generateToken(auth);
            String refreshToken = jwtTokenProvider.generateRefreshToken(auth.getName());

            // Update refresh token vào DB (Cân nhắc dùng mapstruct nếu mapping phức tạp)
            updateUserRefreshToken(request.getEmail(), refreshToken);

            return buildAuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email hoặc mật khẩu không chính xác");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtTokenProvider.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .filter(u -> refreshToken.equals(u.getRefreshToken()))
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ hoặc đã bị thu hồi"));

        // 3. Kiểm tra UserDetails (Trạng thái account: lock, disable...)
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 4. Token Rotation: Tạo cặp token mới
        String newAccessToken = jwtTokenProvider.generateToken(
                UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities()));
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return buildAuthResponse(newAccessToken, newRefreshToken);
    }

    private void updateUserRefreshToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessTokenCookie(
                        createCookie(TokenCookieConstants.ACCESS_TOKEN_COOKIE, accessToken, "/", accessTokenExpiry))
                .refreshTokenCookie(createCookie(TokenCookieConstants.REFRESH_TOKEN_COOKIE, refreshToken,
                        "/api/auth/refresh", refreshTokenExpiry))
                .build();
    }

    private ResponseCookie createCookie(String name, String value, String path, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(path)
                .maxAge(maxAge)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            String email = jwtTokenProvider.extractEmail(refreshToken);
            userRepository.findByEmail(email).ifPresent(user -> {
                if (refreshToken.equals(user.getRefreshToken())) {
                    user.setRefreshToken(null);
                    userRepository.save(user);
                }
            });
        }
    }
}
