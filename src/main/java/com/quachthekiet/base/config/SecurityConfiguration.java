package com.quachthekiet.base.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.quachthekiet.base.security.jwt.CustomJwtAuthenticationConverter;
import com.quachthekiet.base.security.jwt.JwtAccessDeniedHandler;
import com.quachthekiet.base.security.jwt.JwtAlgorithmProvider;
import com.quachthekiet.base.security.jwt.JwtEntryPoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Value("${jwt.base64-secret}")
	private String jwtKey;

	@Value("${spring.cors.allowed-origins}")
	private String allowedOrigins;
	@Value("${spring.cors.allowed-methods}")
	private String allowedMethods;
	@Value("${spring.cors.allowed-headers}")
	private String allowedHeaders;
	@Value("${spring.cors.allow-credentials}")
	private boolean allowCredentials;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private final String[] publicEndpoints = {
			"/api/auth/**",
			"/forgot-password",
			"/confirm-reset-password",
			"/reset-password",
			"/swagger-ui/**", // Cho phép truy cập Swagger UI
			"/v3/api-docs/**", // Cho phép truy cập tài liệu API
			"/swagger-resources/**", // Cho phép truy cập tài nguyên Swagger
			"/webjars/**",
			"swagger-ui.html", // Cho phép truy cập webjars
			"/login/**" // Cho phép truy cập login
	};

	private final String[] adminEndpoints = {
			"/api/admin/**"
	};

	private final String[] userEndpoints = {
			"/api/users/**"
	};

	private final JwtEntryPoint jwtEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAlgorithmProvider jwtAlgorithmProvider;
	private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

	public SecurityConfiguration(JwtEntryPoint jwtEntryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler,
			JwtAlgorithmProvider jwtAlgorithmProvider,
			CustomJwtAuthenticationConverter customJwtAuthenticationConverter) {
		this.jwtEntryPoint = jwtEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.jwtAlgorithmProvider = jwtAlgorithmProvider;
		this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(jwtEntryPoint)
						.accessDeniedHandler(jwtAccessDeniedHandler))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(publicEndpoints).permitAll()
								.requestMatchers("/api/auth/logout").permitAll()
								.requestMatchers(userEndpoints).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
								.requestMatchers(adminEndpoints).hasAnyAuthority("ROLE_ADMIN")
								.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
						.decoder(jwtDecoder())
						.jwtAuthenticationConverter(jwtAuthenticationConverter())))
				.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		// http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
		return http.build();
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(customJwtAuthenticationConverter);
		return converter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(List.of(allowedOrigins.split(","))); // Split by comma if multiple origins
		corsConfiguration.setAllowedMethods(List.of(allowedMethods.split(","))); // Split by comma if multiple methods
		corsConfiguration.setAllowedHeaders(List.of(allowedHeaders.split(","))); // Split by comma if multiple headers
		corsConfiguration.setAllowCredentials(allowCredentials); // Set allow credentials

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withSecretKey(getSecretKey())
				.macAlgorithm(jwtAlgorithmProvider.getMacAlgorithm())
				.build();
	}

	private SecretKey getSecretKey() {
		byte[] keyBytes = Base64.from(jwtKey).decode();
		return new SecretKeySpec(keyBytes, 0, keyBytes.length, jwtAlgorithmProvider.getMacAlgorithm().getName());
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
	}

}
