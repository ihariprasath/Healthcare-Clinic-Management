
package com.ey.dto;

import java.time.OffsetDateTime;

import com.ey.entity.AppointmentType;

public class AppointmentCreateRequest {

	private AppointmentType type;
	private OffsetDateTime slotStart;
	private OffsetDateTime slotEnd;
	private String specialization;
	private Boolean autoAssign; 

	public AppointmentCreateRequest() {
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

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public Boolean getAutoAssign() {
		return autoAssign;
	}

	public void setAutoAssign(Boolean autoAssign) {
		this.autoAssign = autoAssign;
	}

}