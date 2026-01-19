
package com.ey.dto;

public class PreferenceUpdateRequest {

	private Long patientId;
	private Long doctorId;
	private Boolean avoid; 
	private String reason;

	public PreferenceUpdateRequest() {
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

	public Boolean getAvoid() {
		return avoid;
	}

	public void setAvoid(Boolean avoid) {
		this.avoid = avoid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}