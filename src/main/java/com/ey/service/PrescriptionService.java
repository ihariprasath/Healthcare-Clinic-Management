
package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.entity.Prescription;
import com.ey.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

	private final PrescriptionRepository repo;

	public PrescriptionService(PrescriptionRepository repo) {
		this.repo = repo;
	}

	public Prescription create(Prescription p) {
		return repo.save(p);
	}

	public Prescription get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Prescription not found"));
	}
}