
package com.ey.service;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ey.config.JwtService;
import com.ey.dto.AuthResponse;
import com.ey.dto.ChangePasswordRequest;
import com.ey.dto.ForgotPasswordRequest;
import com.ey.dto.LoginRequest;
import com.ey.dto.RegisterAdminRequest;
import com.ey.dto.RegisterRequest;
import com.ey.dto.ResetPasswordOtpRequest;
import com.ey.entity.Role;
import com.ey.entity.User;
import com.ey.repository.UserRepository;

@Service
public class AuthService {

	@Value("${app.admin.secret}")
	private String adminSecret;
	private final UserRepository repo;
	private final PasswordEncoder encoder;
	private final JwtService jwt;

	public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
		this.repo = repo;
		this.encoder = encoder;
		this.jwt = jwt;
	}

	public AuthResponse register(RegisterRequest req) {
		if (repo.existsByEmail(req.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		User u = new User();
		u.setName(req.getName());
		u.setEmail(req.getEmail());
		u.setPassword(encoder.encode(req.getPassword()));
		u.setRoles(Set.of(Role.PATIENT));
		repo.save(u);

		return build(u);
	}

	public AuthResponse registerAdmin(RegisterAdminRequest req) {

//		if (repo.existsByRolesContaining(Role.ADMIN)) {
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Admin already exists, can't create another admin");
//		}
		// ✅ verify secret key
		if (req.getSecretKey() == null || !req.getSecretKey().equals(adminSecret)) {
			throw new RuntimeException("Invalid admin secret key");
		}

		if (repo.existsByEmail(req.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		User u = new User();
		u.setName(req.getName());
		u.setEmail(req.getEmail());
		u.setPassword(encoder.encode(req.getPassword()));
		u.setRoles(Set.of(Role.ADMIN)); // ✅ ADMIN
		repo.save(u);

		return build(u);
	}

	public AuthResponse login(LoginRequest req) {
		User u = repo.findByEmail(req.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email/password"));

		if (!encoder.matches(req.getPassword(), u.getPassword())) {
			throw new RuntimeException("Invalid email/password");
		}

		return build(u);
	}

	public String forgotPasswordOtp(ForgotPasswordRequest req) {

		User user = repo.findByEmail(req.getEmail()).orElse(null);

		if (user == null) {
			return "If the account exists, a reset OTP will be sent.";
		}

		String otp = String.format("%06d", new Random().nextInt(999999));

		user.setResetOtp(otp);
		user.setResetOtpExpiry(OffsetDateTime.now().plusMinutes(10));
		user.setResetOtpAttempts(0);
		repo.save(user);

		System.out.println("✅ Password Reset OTP for " + req.getEmail() + " is: " + otp);

		return "If the account exists, a reset OTP will be sent.";
	}

	public String resetPasswordWithOtp(ResetPasswordOtpRequest req) {

		User user = repo.findByEmail(req.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email/OTP"));

		if (user.getResetOtp() == null || user.getResetOtpExpiry() == null) {
			throw new RuntimeException("OTP not requested");
		}

		if (OffsetDateTime.now().isAfter(user.getResetOtpExpiry())) {
			throw new RuntimeException("OTP expired. Please request again.");
		}

		if (user.getResetOtpAttempts() >= 5) {
			throw new RuntimeException("Too many wrong attempts. OTP locked.");
		}

		if (!req.getOtp().equals(user.getResetOtp())) {
			user.setResetOtpAttempts(user.getResetOtpAttempts() + 1);
			repo.save(user);
			throw new RuntimeException("Invalid OTP");
		}

		user.setPassword(encoder.encode(req.getNewPassword()));

		user.setResetOtp(null);
		user.setResetOtpExpiry(null);
		user.setResetOtpAttempts(0);

		repo.save(user);

		return "Password reset successful";
	}

	public String changePassword(String email, ChangePasswordRequest req) {

		User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (!encoder.matches(req.getCurrentPassword(), user.getPassword())) {
			throw new RuntimeException("Current password incorrect");
		}

		user.setPassword(encoder.encode(req.getNewPassword()));
		repo.save(user);

		return "Password changed successfully";
	}

	private AuthResponse build(User u) {
		String token = jwt.generateToken(u.getEmail());

		AuthResponse res = new AuthResponse();
		res.setMessage("Authentication successful");
		res.setAccessToken(token);
		res.setExpiresAt(OffsetDateTime.now().plusHours(1));

		res.setId(u.getId());
		res.setName(u.getName());
		res.setRoles(u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
		return res;
	}
}