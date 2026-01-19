package com.ey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.MedicalDocument;

public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {

	List<MedicalDocument> findByPatientId(Long PatientId);
}
