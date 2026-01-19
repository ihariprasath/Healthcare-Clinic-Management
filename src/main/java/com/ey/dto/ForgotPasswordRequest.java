
package com.ey.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

	@Email
	@NotBlank
	private String email;

	// "EMAIL" or "SMS" - for now keep EMAIL only
	private String channel = "EMAIL";

	// "OTP" or "TOKEN" - we implement OTP only now
	private String mode = "OTP";

	public ForgotPasswordRequest() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}