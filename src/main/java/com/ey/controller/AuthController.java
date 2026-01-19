

package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.AuthResponse;
import com.ey.dto.ChangePasswordRequest;
import com.ey.dto.ForgotPasswordRequest;
import com.ey.dto.LoginRequest;
import com.ey.dto.RegisterRequest;
import com.ey.dto.ResetPasswordOtpRequest;
import com.ey.exception.ApiResponse;
import com.ey.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(service.register(req), "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.login(req), "Login success"));
    }
    
    @PostMapping("/forgot-password")
public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
    String msg = service.forgotPasswordOtp(req);
    return ResponseEntity.ok(ApiResponse.ok(null, msg));
}

@PostMapping("/reset-password-otp")
public ResponseEntity<ApiResponse<Object>> resetPasswordOtp(@Valid @RequestBody ResetPasswordOtpRequest req) {
    String msg = service.resetPasswordWithOtp(req);
    return ResponseEntity.ok(ApiResponse.ok(null, msg));
}

@PostMapping("/change-password")
public ResponseEntity<ApiResponse<Object>> changePassword(
        Authentication authentication,
        @Valid @RequestBody ChangePasswordRequest req
) {
    String email = authentication.getName();
    String msg = service.changePassword(email, req);
    return ResponseEntity.ok(ApiResponse.ok(null, msg));
}
    
}