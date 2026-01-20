
package com.ey.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.DoctorAvailabilityRequest;
import com.ey.entity.Doctor;
import com.ey.exception.ApiErrorResponse;
import com.ey.service.DoctorAvailabilityService;
import com.ey.service.DoctorService;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

	private final DoctorService service;

	private final DoctorAvailabilityService availabilityService;

	public DoctorController(DoctorService service, DoctorAvailabilityService availabilityService) {
		this.service = service;
		this.availabilityService = availabilityService;
	}

	@PostMapping
	public ResponseEntity<ApiErrorResponse<Doctor>> create(@RequestBody Doctor d) {
		return ResponseEntity.status(201).body(ApiErrorResponse.created(service.create(d), "Doctor created"));
	}

	@GetMapping
	public ResponseEntity<ApiErrorResponse<List<Doctor>>> list(@RequestParam(required = false) String specialization) {
		return ResponseEntity.ok(ApiErrorResponse.ok(service.list(specialization), "Doctors fetched"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiErrorResponse<Doctor>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiErrorResponse.ok(service.get(id), "Doctor fetched"));
	}

	@GetMapping("/{id}/availability")
	public ResponseEntity<ApiErrorResponse<Object>> getAvailability(@PathVariable Long id) {
		return ResponseEntity
				.ok(ApiErrorResponse.ok(availabilityService.getAvailability(id), "Doctor availability fetched"));
	}

	@PutMapping("/{id}/availability")
	public ResponseEntity<ApiErrorResponse<Object>> updateAvailability(@PathVariable Long id,
			@RequestBody List<DoctorAvailabilityRequest> requests) {
		return ResponseEntity.ok(
				ApiErrorResponse.ok(availabilityService.updateAvailability(id, requests), "Doctor availability updated"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiErrorResponse<Doctor>> update(@PathVariable Long id, @RequestBody Doctor d) {
		return ResponseEntity.ok(ApiErrorResponse.ok(service.update(id, d), "Doctor updated"));
	}
}