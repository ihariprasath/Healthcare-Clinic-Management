

package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    List<Review> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    Optional<Review> findByAppointmentId(Long appointmentId);
}