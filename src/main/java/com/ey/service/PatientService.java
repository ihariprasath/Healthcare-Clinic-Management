package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.dto.request.PatientRequest;
import com.ey.entity.Patient;
import com.ey.repository.PatientRepository;

@Service
public class PatientService {

	private final PatientRepository patientrepository;

	public PatientService(PatientRepository patientrepository) {
		this.patientrepository = patientrepository;
	}
	
	public Patient createPatient(PatientRequest request) {
		Patient  patient = new Patient();
		patient.setName(request.getName());
		patient.setPhone(request.getPhone());
		patient.setGender(request.getGender());
		patient.setDob(request.getDob());
		
		return patientrepository.save(patient);
	}
	
	public Patient getPatient(Long id) {
		return patientrepository.findById(id).orElseThrow();
	}
	
}
