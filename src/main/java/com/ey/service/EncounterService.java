
package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.entity.Encounter;
import com.ey.repository.EncounterRepository;

@Service
public class EncounterService {

	private final EncounterRepository repo;

	public EncounterService(EncounterRepository repo) {
		this.repo = repo;
	}

	public Encounter create(Encounter e) {
		return repo.save(e);
	}

	public Encounter get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Encounter not found"));
	}
}