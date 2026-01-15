package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.dto.request.DoctorRequest;
import com.ey.entity.Doctor;
import com.ey.repository.DoctorRepository;

@Service
public class DoctorService {
	
	private final DoctorRepository doctorRepository;
	
	public DoctorService(DoctorRepository doctorRepository) {
		this.doctorRepository = doctorRepository;
	}
	
	public Doctor createDoctor(DoctorRequest request) {
		Doctor doctor = new Doctor();
		doctor.setName(request.getName());
		doctor.setSpecialization(request.getSpecialization());
		doctor.setPhone(request.getPhone());
		
		return doctorRepository.save(doctor);
	}
	
	public Doctor getDoctor(Long id) {
		return doctorRepository.findById(id).orElseThrow();
	}
}
