package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.AppointmentRequest;
import com.ey.entity.Appointment;
import com.ey.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}
	
	@PostMapping
	public ResponseEntity<Appointment> create(@RequestBody AppointmentRequest request){
		return ResponseEntity.ok(appointmentService.bookAppointment(request));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Appointment> get(@PathVariable Long id){
		return ResponseEntity.ok(appointmentService.getAppointment(id));
	}
}