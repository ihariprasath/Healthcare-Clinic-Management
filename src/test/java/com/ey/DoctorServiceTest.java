package com.ey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ey.entity.Doctor;
import com.ey.repository.DoctorRepository;
import com.ey.service.DoctorService;

class DoctorServiceTest {

	@Mock
	private DoctorRepository doctorRepo;

	private DoctorService doctorService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		doctorService = new DoctorService(doctorRepo);
	}

	@Test
	void getAllDoctors_success() {
		Doctor d1 = new Doctor();
		d1.setId(1L);
		d1.setName("Doc1");
		Doctor d2 = new Doctor();
		d2.setId(2L);
		d2.setName("Doc2");

		when(doctorRepo.findAll()).thenReturn(Arrays.asList(d1, d2));

		List<Doctor> list = doctorService.list(null);

		assertNotNull(list);
		assertEquals(2, list.size());
		verify(doctorRepo, times(1)).findAll();
	}

	@Test
	void getBySpecialization_success() {
		Doctor d1 = new Doctor();
		d1.setId(1L);
		d1.setName("Doc1");
		d1.setSpecialization("Cardiology");

		when(doctorRepo.findBySpecializationIgnoreCase("Cardiology")).thenReturn(List.of(d1));

		List<Doctor> result = doctorService.list("Cardiology");

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Cardiology", result.get(0).getSpecialization());

		verify(doctorRepo, times(1)).findBySpecializationIgnoreCase("Cardiology");
	}
}