
package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.entity.Patient;
import com.ey.repository.PatientRepository;

@Service
public class PatientService {

	private final PatientRepository repo;

	public PatientService(PatientRepository repo) {
		this.repo = repo;
	}

	public Patient create(Patient p) {
		return repo.save(p);
	}

	public Patient update(Long id, Patient p) {
		Patient old = get(id);
		p.setId(old.getId());
		return repo.save(p);
	}

	public Patient get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
	}
}