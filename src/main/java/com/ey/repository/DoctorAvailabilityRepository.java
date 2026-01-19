
package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.DoctorAvailability;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorId(Long doctorId);

    void deleteByDoctorId(Long doctorId);

    List<DoctorAvailability> findByDoctorIdAndDayOfWeek(Long doctorId, String dayOfWeek);
}