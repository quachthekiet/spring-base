package com.quachthekiet.base.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.quachthekiet.base.service.RedisService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RedisService redisService;

    public CustomJwtAuthenticationConverter(RedisService redisService) {
        this.redisService = redisService;
    }


    // Chuyển đổi Jwt thành danh sách quyền
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        String jti = jwt.getId();

       if (jti != null && redisService.isInBlacklist(jti)) {
           throw new BadCredentialsException("Token đã bị revoke (blacklisted)");
       }


        List<?> roles = jwt.getClaim("roles");

        if (roles == null) {
            return List.of();
        }

        // Trường hợp roles trong token có dạng [{ "role": "USER" }]
        return roles.stream()
                .map(obj -> ((java.util.Map<?, ?>) obj).get("role").toString())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

}
