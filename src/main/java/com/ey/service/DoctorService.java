
package com.ey.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.entity.Doctor;
import com.ey.repository.DoctorRepository;

@Service
public class DoctorService {

	private final DoctorRepository repo;

	public DoctorService(DoctorRepository repo) {
		this.repo = repo;
	}

	public Doctor create(Doctor d) {
		return repo.save(d);
	}

	public Doctor get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
	}

	public List<Doctor> list(String specialization) {
		if (specialization == null || specialization.isBlank()) {
			return repo.findAll();
		}
		return repo.findBySpecializationIgnoreCase(specialization);
	}

	public Doctor update(Long id, Doctor d) {
		Doctor old = get(id);
		d.setId(old.getId());
		return repo.save(d);
	}
}