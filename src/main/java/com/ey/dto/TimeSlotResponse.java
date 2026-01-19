
package com.ey.dto;

public class TimeSlotResponse {

	private Long doctorId;
	private String doctorName;
	private String start;
	private String end;

	public TimeSlotResponse() {
	}

	public TimeSlotResponse(Long doctorId, String doctorName, String start, String end) {
		this.doctorId = doctorId;
		this.doctorName = doctorName;
		this.start = start;
		this.end = end;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
}