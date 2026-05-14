package com.quachthekiet.base.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.ServiceUnavailableException;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.quachthekiet.base.common.RestResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<RestResponse<?>> handleServiceUnavailableException(ServiceUnavailableException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        RestResponse<Map<String, String>> response = new RestResponse<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.setMessage(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestResponse<?>> handleNotFoundException(NotFoundException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.NOT_FOUND.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse<?>> handleBadCredentialsException(BadCredentialsException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<RestResponse<?>> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<RestResponse<?>> handleTokenRevokedException(TokenRevokedException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<?>> handleAuthenticationException(AuthenticationException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.FORBIDDEN.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<RestResponse<?>> handleInvalidTokenException(InvalidTokenException e) {
        RestResponse<String> response = new RestResponse<>();
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
