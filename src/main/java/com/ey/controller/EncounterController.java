
package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.entity.Encounter;
import com.ey.exception.ApiResponse;
import com.ey.service.EncounterService;

@RestController
@RequestMapping("/api/v1/encounters")
public class EncounterController {

	private final EncounterService service;

	public EncounterController(EncounterService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Encounter>> create(@RequestBody Encounter e) {
		return ResponseEntity.status(201).body(ApiResponse.created(service.create(e), "Encounter created"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Encounter>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "Encounter fetched"));
	}
}