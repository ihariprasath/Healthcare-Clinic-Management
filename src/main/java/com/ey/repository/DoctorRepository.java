
package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Doctor;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
}