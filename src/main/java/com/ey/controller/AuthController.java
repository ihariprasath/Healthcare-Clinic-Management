
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
import com.ey.dto.RegisterAdminRequest;
import com.ey.dto.RegisterRequest;
import com.ey.dto.ResetPasswordOtpRequest;
import com.ey.exception.ApiErrorResponse;
import com.ey.service.AuthService;
import com.ey.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService service;
	private final TokenBlacklistService tokenBlacklistService;

	public AuthController(AuthService service, TokenBlacklistService tokenBlacklistService) {
		this.service = service;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiErrorResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
		return ResponseEntity.status(201).body(ApiErrorResponse.created(service.register(req), "User registered"));
	}

	@PostMapping("/register-admin")
	public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterAdminRequest req) {
		return ResponseEntity.status(201).body(service.registerAdmin(req));
//        return ApiResponse.created(service.registerAdmin(req), "Admin created");
	}

	@PostMapping("/login")
	public ResponseEntity<ApiErrorResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
		return ResponseEntity.ok(ApiErrorResponse.ok(service.login(req), "Login success"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiErrorResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
		String msg = service.forgotPasswordOtp(req);
		return ResponseEntity.ok(ApiErrorResponse.ok(null, msg));
	}

	@PostMapping("/reset-password-otp")
	public ResponseEntity<ApiErrorResponse<Object>> resetPasswordOtp(@Valid @RequestBody ResetPasswordOtpRequest req) {
		String msg = service.resetPasswordWithOtp(req);
		return ResponseEntity.ok(ApiErrorResponse.ok(null, msg));
	}

	@PostMapping("/change-password")
	public ResponseEntity<ApiErrorResponse<Object>> changePassword(Authentication authentication,
			@Valid @RequestBody ChangePasswordRequest req) {
		String email = authentication.getName();
		String msg = service.changePassword(email, req);
		return ResponseEntity.ok(ApiErrorResponse.ok(null, msg));
	}

	@PostMapping("/logout")
	public ApiErrorResponse<String> logout(HttpServletRequest request) {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing token");
		}

		String token = authHeader.substring(7);
		tokenBlacklistService.blacklist(token);

		return ApiErrorResponse.ok("Logged out", "Logout successful");
	}
}
