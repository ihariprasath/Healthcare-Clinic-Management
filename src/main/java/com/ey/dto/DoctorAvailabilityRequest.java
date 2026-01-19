
package com.ey.dto;

public class DoctorAvailabilityRequest {

	private String dayOfWeek; // MON...
	private String startTime; // "09:00"
	private String endTime; // "13:00"

	public DoctorAvailabilityRequest() {
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}