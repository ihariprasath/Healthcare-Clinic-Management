package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.DoctorRequest;
import com.ey.entity.Doctor;
import com.ey.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
	
	private final DoctorService doctorService;
	
	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}
	
	@PostMapping
	public ResponseEntity<Doctor> create(@RequestBody DoctorRequest request){
		return ResponseEntity.ok(doctorService.createDoctor(request));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Doctor> get(@PathVariable Long id){
		return ResponseEntity.ok(doctorService.getDoctor(id));
	}
}
