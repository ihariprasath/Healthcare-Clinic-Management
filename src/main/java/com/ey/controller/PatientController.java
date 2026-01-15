package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.request.PatientRequest;
import com.ey.entity.Patient;
import com.ey.service.PatientService;

@RestController
@RequestMapping("/patients")
public class PatientController {
	
	private final PatientService patientService;
	
	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}
	
	@PostMapping
	public ResponseEntity<Patient> create(@RequestBody PatientRequest request){
		return ResponseEntity.ok(patientService.createPatient(request));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Patient> get(@PathVariable Long id){
		return ResponseEntity.ok(patientService.getPatient(id));
	}
}
