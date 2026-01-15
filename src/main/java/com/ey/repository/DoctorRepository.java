package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
