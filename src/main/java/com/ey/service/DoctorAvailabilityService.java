package com.ey.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.dto.DoctorAvailabilityRequest;
import com.ey.entity.Doctor;
import com.ey.entity.DoctorAvailability;
import com.ey.repository.DoctorAvailabilityRepository;
import com.ey.repository.DoctorRepository;

@Service
public class DoctorAvailabilityService {

	private final DoctorAvailabilityRepository repo;
	private final DoctorRepository doctorRepo;

	public DoctorAvailabilityService(DoctorAvailabilityRepository repo, DoctorRepository doctorRepo) {
		this.repo = repo;
		this.doctorRepo = doctorRepo;
	}

	public List<DoctorAvailability> getAvailability(Long doctorId) {
		doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
		return repo.findByDoctorId(doctorId);
	}

	@Transactional
	public List<DoctorAvailability> updateAvailability(Long doctorId, List<DoctorAvailabilityRequest> requests) {

		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));

		// delete existing timings
		repo.deleteByDoctorId(doctor.getId());

		List<DoctorAvailability> saved = new ArrayList<>();

		for (DoctorAvailabilityRequest r : requests) {
			DoctorAvailability da = new DoctorAvailability();
			da.setDoctorId(doctor.getId());
			da.setDayOfWeek(r.getDayOfWeek());
			da.setStartTime(r.getStartTime());
			da.setEndTime(r.getEndTime());
			saved.add(repo.save(da));
		}

		return saved;
	}
}