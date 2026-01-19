package com.ey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.PatientDoctorPreference;

public interface PatientDoctorPreferenceRepository extends JpaRepository<PatientDoctorPreference, Long>{

	List<PatientDoctorPreference> findByPatientId(Long PatientId);
	
	Optional<PatientDoctorPreference> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
}
