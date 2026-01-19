
package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Appointment;
import com.ey.entity.AppointmentStatus;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndStatusAndSlotStartLessThanAndSlotEndGreaterThan(
            Long doctorId,
            AppointmentStatus status,
            OffsetDateTime end,
            OffsetDateTime start
    );
     List<Appointment> findByDoctorIdAndSlotStartBetweenAndStatusNot(
            Long doctorId,
            OffsetDateTime from,
            OffsetDateTime to,
            AppointmentStatus status
    );
}