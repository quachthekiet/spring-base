package com.quachthekiet.base.service;

import com.quachthekiet.base.model.AuthRequest;
import com.quachthekiet.base.model.AuthResponse;

public interface AuthService {
    AuthResponse authenticate(AuthRequest authRequest);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}
