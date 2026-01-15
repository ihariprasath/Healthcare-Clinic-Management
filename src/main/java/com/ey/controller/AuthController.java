package com.ey.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.AuthRequest;
import com.ey.entity.Role;
import com.ey.entity.User;
import com.ey.repository.UserRepository;
import com.ey.util.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, com.ey.util.JwtUtil jwtUtil) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody AuthRequest request) {

		User user = new User();
		user.setEmail(request.getEmail().toLowerCase().trim());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(Set.of(Role.PATIENT));

		userRepository.save(user);
		return ResponseEntity.status(201).body("User Registered");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {

		User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.status(401).body("Invalid Credentials");
		}

		String token = jwtUtil.generateToken(
				user.getEmail(),
				user.getRoles().iterator().next().name());

		return ResponseEntity.ok(Map.of("accessToken", token));

	}
	
}
