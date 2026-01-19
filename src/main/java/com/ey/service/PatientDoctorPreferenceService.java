package com.ey.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.dto.PreferenceUpdateRequest;
import com.ey.entity.PatientDoctorPreference;
import com.ey.repository.PatientDoctorPreferenceRepository;

@Service
public class PatientDoctorPreferenceService {

	private final PatientDoctorPreferenceRepository repo;

	public PatientDoctorPreferenceService(PatientDoctorPreferenceRepository repo) {
		this.repo = repo;
	}

	public List<PatientDoctorPreference> getByPatient(Long patientId) {
		return repo.findByPatientId(patientId);
	}

	@Transactional
	public PatientDoctorPreference updatePreference(PreferenceUpdateRequest req) {

		if (req.getPatientId() == null || req.getDoctorId() == null) {
			throw new RuntimeException("patientId and doctorId are required");
		}

		PatientDoctorPreference pref = repo.findByPatientIdAndDoctorId(req.getPatientId(), req.getDoctorId())
				.orElse(new PatientDoctorPreference());

		pref.setPatientId(req.getPatientId());
		pref.setDoctorId(req.getDoctorId());
		pref.setAvoid(req.getAvoid() != null && req.getAvoid());
		pref.setReason(req.getReason() == null ? "MANUAL" : req.getReason());
		pref.setUpdatedAt(LocalDateTime.now());

		return repo.save(pref);
	}

	@Transactional
	public void avoidDoctorFromNegativeReview(Long patientId, Long doctorId) {

		PatientDoctorPreference pref = repo.findByPatientIdAndDoctorId(patientId, doctorId)
				.orElse(new PatientDoctorPreference());

		pref.setPatientId(patientId);
		pref.setDoctorId(doctorId);
		pref.setAvoid(true);
		pref.setReason("NEGATIVE_REVIEW");
		pref.setUpdatedAt(LocalDateTime.now());

		repo.save(pref);
	}

	public boolean isDoctorAvoided(Long patientId, Long doctorId) {
		return repo.findByPatientIdAndDoctorId(patientId, doctorId).map(p -> Boolean.TRUE.equals(p.getAvoid()))
				.orElse(false);
	}
}