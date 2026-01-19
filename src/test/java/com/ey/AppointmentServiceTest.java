package com.ey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ey.dto.AppointmentCreateRequest;
import com.ey.entity.Appointment;
import com.ey.entity.AppointmentStatus;
import com.ey.entity.AppointmentType;
import com.ey.repository.AppointmentRepository;
import com.ey.repository.DoctorAvailabilityRepository;
import com.ey.repository.DoctorRepository;
import com.ey.service.AppointmentService;

class AppointmentServiceTest {

	@Mock
	private AppointmentRepository repo;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private DoctorAvailabilityRepository availabilityRepository;

	@InjectMocks
	private AppointmentService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createAppointment_success() {
		Long patientId = 3L;
		Long doctorId = 1L;

		AppointmentCreateRequest req = new AppointmentCreateRequest();
		req.setType(AppointmentType.IN_PERSON);
		req.setSlotStart(OffsetDateTime.parse("2026-02-10T10:00:00+05:30"));
		req.setSlotEnd(OffsetDateTime.parse("2026-02-10T10:20:00+05:30"));

		when(repo.existsByDoctorIdAndStatusAndSlotStartLessThanAndSlotEndGreaterThan(eq(doctorId),
				eq(AppointmentStatus.SCHEDULED), any(), any())).thenReturn(false);

		Appointment saved = new Appointment();
		saved.setId(100L);
		saved.setPatientId(patientId);
		saved.setDoctorId(doctorId);
		saved.setStatus(AppointmentStatus.SCHEDULED);

		when(repo.save(any(Appointment.class))).thenReturn(saved);

		Appointment result = service.create(patientId, doctorId, req);

		assertNotNull(result);
		assertEquals(100L, result.getId());
		assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());

		verify(repo, times(1)).save(any(Appointment.class));
	}

	@Test
	void createAppointment_conflict_throwsException() {
		Long patientId = 3L;
		Long doctorId = 1L;

		AppointmentCreateRequest req = new AppointmentCreateRequest();
		req.setSlotStart(OffsetDateTime.now());
		req.setSlotEnd(OffsetDateTime.now().plusMinutes(20));

		when(repo.existsByDoctorIdAndStatusAndSlotStartLessThanAndSlotEndGreaterThan(eq(doctorId),
				eq(AppointmentStatus.SCHEDULED), any(), any())).thenReturn(true);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> service.create(patientId, doctorId, req));
		assertTrue(ex.getMessage().contains("Slot already booked"));

		verify(repo, never()).save(any());
	}

	@Test
	void getAppointment_success() {
		Appointment a = new Appointment();
		a.setId(1L);

		when(repo.findById(1L)).thenReturn(Optional.of(a));

		Appointment result = service.get(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void getAppointment_notFound_throwsException() {
		when(repo.findById(999L)).thenReturn(Optional.empty());

		RuntimeException ex = assertThrows(RuntimeException.class, () -> service.get(999L));
		assertTrue(ex.getMessage().contains("Appointment not found"));
	}

	@Test
	void cancelAppointment_success() {
		Appointment a = new Appointment();
		a.setId(1L);
		a.setStatus(AppointmentStatus.SCHEDULED);

		when(repo.findById(1L)).thenReturn(Optional.of(a));
		when(repo.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

		Appointment result = service.cancel(1L);

		assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
		verify(repo, times(1)).save(a);
	}
}