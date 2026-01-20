package com.ey.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.ReviewCreateRequest;
import com.ey.entity.Review;
import com.ey.entity.User;
import com.ey.exception.ApiErrorResponse;
import com.ey.repository.UserRepository;
import com.ey.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService service;
	private final UserRepository userRepo;

	public ReviewController(ReviewService service, UserRepository userRepo) {
		this.service = service;
		this.userRepo = userRepo;
	}

	@PostMapping
	public ApiErrorResponse<Review> create(Authentication auth, @RequestBody ReviewCreateRequest req) {

		String email = auth.getName();

		User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		Long patientId = user.getPatientId();

		Review saved = service.createReview(patientId, req);

		return ApiErrorResponse.ok(saved, "Review submitted");
	}

	@GetMapping
	public ApiErrorResponse<List<Review>> getAll() {
		List<Review> reviews = service.getAll();
		return ApiErrorResponse.ok(reviews, "Reviews fetched");
	}

	@GetMapping("/doctor/{doctorId}")
	public ApiErrorResponse<List<Review>> getDoctorReviews(@PathVariable Long doctorId) {
		List<Review> reviews = service.getByDoctor(doctorId);
		return ApiErrorResponse.ok(reviews, "Doctor reviews fetched");
	}

	@GetMapping("/my")
	public ApiErrorResponse<List<Review>> my(Authentication auth) {

		String email = auth.getName();

		User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Long patientId = user.getPatientId();

		List<Review> reviews = service.getMyReviews(patientId);

		return ApiErrorResponse.ok(reviews, "My reviews fetched");
	}
}