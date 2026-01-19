
package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.entity.Patient;
import com.ey.exception.ApiResponse;
import com.ey.service.PatientService;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

	private final PatientService service;

	public PatientController(PatientService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Patient>> create(@RequestBody Patient p) {
		return ResponseEntity.status(201).body(ApiResponse.created(service.create(p), "Patient created"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Patient>> update(@PathVariable Long id, @RequestBody Patient p) {
		return ResponseEntity.ok(ApiResponse.ok(service.update(id, p), "Patient updated"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Patient>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "Patient fetched"));
	}
}