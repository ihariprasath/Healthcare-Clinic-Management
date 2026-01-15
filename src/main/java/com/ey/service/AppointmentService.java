package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.dto.request.AppointmentRequest;
import com.ey.entity.Appointment;
import com.ey.entity.Doctor;
import com.ey.entity.Patient;
import com.ey.repository.AppointmentRepository;
import com.ey.repository.DoctorRepository;
import com.ey.repository.PatientRepository;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private PatientRepository patientRepository;
	private DoctorRepository doctorRepository;

	public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
			DoctorRepository doctorRepository) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
	}

	public Appointment bookAppointment(AppointmentRequest request) {

		Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow();

		Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow();

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);
		appointment.setAppointmentDate(request.getAppointmentDate());
		appointment.setAppointmentTime(request.getAppointmentTime());
		appointment.setStatus("Booked");

		return appointmentRepository.save(appointment);
	}

	public Appointment getAppointment(Long id) {
		return appointmentRepository.findById(id).orElseThrow();
	}

}
