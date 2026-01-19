package com.ey.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.dto.ReviewCreateRequest;
import com.ey.entity.Appointment;
import com.ey.entity.AppointmentStatus;
import com.ey.entity.Doctor;
import com.ey.entity.Review;
import com.ey.repository.AppointmentRepository;
import com.ey.repository.DoctorRepository;
import com.ey.repository.ReviewRepository;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepo;
	private final AppointmentRepository appointmentRepo;
	private final DoctorRepository doctorRepo;
	private final PatientDoctorPreferenceService prefService;

	public ReviewService(ReviewRepository reviewRepo, AppointmentRepository appointmentRepo,
			DoctorRepository doctorRepo, PatientDoctorPreferenceService prefService) {
		this.reviewRepo = reviewRepo;
		this.appointmentRepo = appointmentRepo;
		this.doctorRepo = doctorRepo;
		this.prefService = prefService;
	}

	@Transactional
	public Review createReview(Long patientId, ReviewCreateRequest req) {

		if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
			throw new RuntimeException("Rating must be between 1 and 5");
		}

		if (req.getAppointmentId() == null) {
			throw new RuntimeException("appointmentId must not be null");
		}

		// ✅ Prevent duplicate review for same appointment
		reviewRepo.findByAppointmentId(req.getAppointmentId()).ifPresent(r -> {
			throw new RuntimeException("Review already submitted for this appointment");
		});

		// ✅ Validate appointment
		Appointment appt = appointmentRepo.findById(req.getAppointmentId())
				.orElseThrow(() -> new RuntimeException("Appointment not found"));

		if (!appt.getPatientId().equals(patientId)) {
			throw new RuntimeException("You can review only your appointment");
		}

		if (appt.getStatus() != AppointmentStatus.CHECKED_IN && appt.getStatus() != AppointmentStatus.COMPLETED) {
			throw new RuntimeException("Review allowed only after consultation (CHECKED_IN/COMPLETED)");
		}

		Long doctorId = appt.getDoctorId();

		// ✅ Create review
		Review review = new Review();
		review.setAppointmentId(appt.getId());
		review.setDoctorId(doctorId);
		review.setPatientId(patientId);
		review.setRating(req.getRating());
		review.setComment(req.getComment());

		if (req.getTags() != null && !req.getTags().isEmpty()) {
			review.setTags(String.join(",", req.getTags()));
		}

		Review saved = reviewRepo.save(review);

		// ✅ Update doctor rating summary
		updateDoctorRating(doctorId, req.getRating());

		// ✅ NEW: negative review => auto avoid doctor
		if (req.getRating() <= 2) {
			prefService.avoidDoctorFromNegativeReview(patientId, doctorId);
		}

		return saved;
	}

	private void updateDoctorRating(Long doctorId, int newRating) {

		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));

		double currentAvg = doctor.getAvgRating() == null ? 0.0 : doctor.getAvgRating();
		int count = doctor.getRatingsCount() == null ? 0 : doctor.getRatingsCount();

		double updatedAvg = ((currentAvg * count) + newRating) / (count + 1);

		doctor.setAvgRating(Math.round(updatedAvg * 10.0) / 10.0); // 1 decimal
		doctor.setRatingsCount(count + 1);

		doctorRepo.save(doctor);
	}

	public List<Review> getAll() {
		return reviewRepo.findAll();
	}

	public List<Review> getByDoctor(Long doctorId) {
		return reviewRepo.findByDoctorIdOrderByCreatedAtDesc(doctorId);
	}

	public List<Review> getMyReviews(Long patientId) {
		return reviewRepo.findByPatientIdOrderByCreatedAtDesc(patientId);
	}
}