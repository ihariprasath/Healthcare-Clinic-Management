
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

import com.ey.dto.AppointmentCreateRequest;
import com.ey.dto.TimeSlotResponse;
import com.ey.entity.Appointment;
import com.ey.exception.ApiResponse;
import com.ey.service.AppointmentService;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

	private final AppointmentService service;

	public AppointmentController(AppointmentService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Appointment>> create(@RequestParam Long patientId,
			@RequestParam(required = false) Long doctorId, @RequestBody AppointmentCreateRequest req) {
		return ResponseEntity.status(201)
				.body(ApiResponse.created(service.create(patientId, doctorId, req), "Appointment created"));
	}

	@GetMapping("/availability")
	public ApiResponse<List<TimeSlotResponse>> availability(@RequestParam String specialization,
			@RequestParam String date) {
		List<TimeSlotResponse> slots = service.getAvailability(specialization, date);
		return ApiResponse.ok(slots, "Slots fetched");
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Appointment>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "Appointment fetched"));
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<ApiResponse<Appointment>> cancel(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.cancel(id), "Appointment cancelled"));
	}

	@PostMapping("/{id}/checkin")
	public ResponseEntity<ApiResponse<Appointment>> checkin(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.checkin(id), "Checked in"));
	}
}