package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

}
