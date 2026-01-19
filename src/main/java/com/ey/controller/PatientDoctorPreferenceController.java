package com.ey.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.dto.PreferenceUpdateRequest;
import com.ey.entity.PatientDoctorPreference;
import com.ey.exception.ApiResponse;
import com.ey.service.PatientDoctorPreferenceService;

@RestController
@RequestMapping("/api/v1/preferences")
public class PatientDoctorPreferenceController {

	private final PatientDoctorPreferenceService service;

	public PatientDoctorPreferenceController(PatientDoctorPreferenceService service) {
		this.service = service;
	}

	@PutMapping
	public ApiResponse<PatientDoctorPreference> update(@RequestBody PreferenceUpdateRequest req) {
		return ApiResponse.ok(service.updatePreference(req), "Preference updated");
	}

	@GetMapping("/patient/{patientId}")
	public ApiResponse<List<PatientDoctorPreference>> getByPatient(@PathVariable Long patientId) {
		return ApiResponse.ok(service.getByPatient(patientId), "Preferences fetched");
	}
}