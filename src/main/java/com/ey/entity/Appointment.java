package com.ey.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments", indexes = { @Index(name = "idx_doc_slot", columnList = "doctorId,slotStart") })
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long patientId;
	private Long doctorId;

	@Enumerated(EnumType.STRING)
	private AppointmentStatus status = AppointmentStatus.SCHEDULED;

	@Enumerated(EnumType.STRING)
	private AppointmentType type = AppointmentType.IN_PERSON;

	private OffsetDateTime slotStart;
	private OffsetDateTime slotEnd;

	public Appointment() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public OffsetDateTime getSlotStart() {
		return slotStart;
	}

	public void setSlotStart(OffsetDateTime slotStart) {
		this.slotStart = slotStart;
	}

	public OffsetDateTime getSlotEnd() {
		return slotEnd;
	}

	public void setSlotEnd(OffsetDateTime slotEnd) {
		this.slotEnd = slotEnd;
	}
}