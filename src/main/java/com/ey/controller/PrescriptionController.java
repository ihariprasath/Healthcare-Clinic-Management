
package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.entity.Prescription;
import com.ey.exception.ApiResponse;
import com.ey.service.PrescriptionService;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

	private final PrescriptionService service;

	public PrescriptionController(PrescriptionService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Prescription>> create(@RequestBody Prescription p) {
		return ResponseEntity.status(201).body(ApiResponse.created(service.create(p), "Prescription created"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Prescription>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "Prescription fetched"));
	}

	@GetMapping("/{id}/pdf")
	public ResponseEntity<ApiResponse<Prescription>> pdf(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "PDF skipped - returning JSON"));
	}
}