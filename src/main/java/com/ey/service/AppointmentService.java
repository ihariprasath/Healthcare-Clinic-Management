package com.ey.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.dto.AppointmentCreateRequest;
import com.ey.dto.TimeSlotResponse;
import com.ey.entity.Appointment;
import com.ey.entity.AppointmentStatus;
import com.ey.entity.AppointmentType;
import com.ey.entity.Doctor;
import com.ey.entity.DoctorAvailability;
import com.ey.repository.AppointmentRepository;
import com.ey.repository.DoctorAvailabilityRepository;
import com.ey.repository.DoctorRepository;

@Service
public class AppointmentService {

	private final AppointmentRepository repo;
	private final DoctorRepository doctorRepository;
	private final DoctorAvailabilityRepository availabilityRepository;

	public AppointmentService(AppointmentRepository repo, DoctorRepository doctorRepository,
			DoctorAvailabilityRepository availabilityRepository) {
		this.repo = repo;
		this.doctorRepository = doctorRepository;
		this.availabilityRepository = availabilityRepository;
	}

	public Appointment create(Long patientId, Long doctorId, AppointmentCreateRequest req) {

		if (req.getAutoAssign() != null && req.getAutoAssign()) {

			if (req.getSpecialization() == null || req.getSpecialization().isBlank()) {
				throw new RuntimeException("specialization is required for autoAssign");
			}

			doctorId = autoAssignDoctor(req.getSpecialization(), req.getSlotStart(), req.getSlotEnd());
		} else {
			if (doctorId == null) {
				throw new RuntimeException("doctorId is required when autoAssign is false");
			}
		}

		boolean conflict = repo.existsByDoctorIdAndStatusAndSlotStartLessThanAndSlotEndGreaterThan(doctorId,
				AppointmentStatus.SCHEDULED, req.getSlotEnd(), req.getSlotStart());

		if (conflict) {
			throw new RuntimeException("Slot already booked (409 conflict)");
		}

		Appointment a = new Appointment();
		a.setPatientId(patientId);
		a.setDoctorId(doctorId);
		a.setType(req.getType() == null ? AppointmentType.IN_PERSON : req.getType());
		a.setSlotStart(req.getSlotStart());
		a.setSlotEnd(req.getSlotEnd());
		a.setStatus(AppointmentStatus.SCHEDULED);

		return repo.save(a);
	}

	public Appointment get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
	}

	public Appointment cancel(Long id) {
		Appointment a = get(id);
		a.setStatus(AppointmentStatus.CANCELLED);
		return repo.save(a);
	}

	public Appointment checkin(Long id) {
		Appointment a = get(id);
		a.setStatus(AppointmentStatus.CHECKED_IN);
		return repo.save(a);
	}

	public List<TimeSlotResponse> getAvailability(String specialization, String date) {

		LocalDate localDate = LocalDate.parse(date);

		String day = localDate.getDayOfWeek().toString().substring(0, 3);

		List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization);

		List<TimeSlotResponse> slots = new ArrayList<>();

		for (Doctor doctor : doctors) {

			List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorIdAndDayOfWeek(doctor.getId(),
					day);

			for (DoctorAvailability a : availabilities) {

				LocalTime start = LocalTime.parse(a.getStartTime());
				LocalTime end = LocalTime.parse(a.getEndTime());

				while (start.plusMinutes(20).compareTo(end) <= 0) {

					LocalDateTime slotStart = LocalDateTime.of(localDate, start);
					LocalDateTime slotEnd = LocalDateTime.of(localDate, start.plusMinutes(20));

					slots.add(new TimeSlotResponse(doctor.getId(), doctor.getName(), slotStart.toString(),
							slotEnd.toString()));

					start = start.plusMinutes(20);
				}
			}
		}

		return slots;
	}

	private boolean isDoctorAvailableForSlot(Long doctorId, String specialization, java.time.OffsetDateTime slotStart,
			java.time.OffsetDateTime slotEnd) {

		Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));

		if (specialization != null && !doctor.getSpecialization().equalsIgnoreCase(specialization)) {
			return false;
		}

		java.time.LocalDate date = slotStart.toLocalDate();
		String day = date.getDayOfWeek().toString().substring(0, 3);

		List<DoctorAvailability> availList = availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, day);

		if (availList.isEmpty()) {
			return false;
		}

		java.time.LocalTime st = slotStart.toLocalTime();
		java.time.LocalTime en = slotEnd.toLocalTime();

		boolean withinAnyAvailability = false;

		for (DoctorAvailability a : availList) {
			java.time.LocalTime aStart = java.time.LocalTime.parse(a.getStartTime());
			java.time.LocalTime aEnd = java.time.LocalTime.parse(a.getEndTime());

			if ((st.equals(aStart) || st.isAfter(aStart)) && (en.equals(aEnd) || en.isBefore(aEnd))) {
				withinAnyAvailability = true;
				break;
			}
		}

		if (!withinAnyAvailability)
			return false;

		boolean conflict = repo.existsByDoctorIdAndStatusAndSlotStartLessThanAndSlotEndGreaterThan(doctorId,
				AppointmentStatus.SCHEDULED, slotEnd, slotStart);

		return !conflict;
	}

	private Long autoAssignDoctor(String specialization, java.time.OffsetDateTime slotStart,
			java.time.OffsetDateTime slotEnd) {

		List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization);

		if (doctors.isEmpty()) {
			throw new RuntimeException("No doctors available for specialization: " + specialization);
		}

		Long selectedDoctorId = null;
		double bestScore = -999999;

		for (Doctor d : doctors) {

			if (!isDoctorAvailableForSlot(d.getId(), specialization, slotStart, slotEnd)) {
				continue;
			}

			OffsetDateTime from = slotStart.toLocalDate().atStartOfDay().atOffset(slotStart.getOffset());

			OffsetDateTime to = slotStart.toLocalDate().atTime(23, 59).atOffset(slotStart.getOffset());

			int workload = repo
					.findByDoctorIdAndSlotStartBetweenAndStatusNot(d.getId(), from, to, AppointmentStatus.CANCELLED)
					.size();

			double rating = (d.getAvgRating() == null) ? 4.0 : d.getAvgRating();

			double score = (rating * 10) - workload;

			if (score > bestScore) {
				bestScore = score;
				selectedDoctorId = d.getId();
			}
		}

		if (selectedDoctorId == null) {
			throw new RuntimeException("No doctor available for selected time slot");
		}

		return selectedDoctorId;
	}

}