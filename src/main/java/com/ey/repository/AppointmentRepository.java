package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

}
