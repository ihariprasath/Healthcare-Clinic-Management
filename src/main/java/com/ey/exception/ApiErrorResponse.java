
package com.ey.exception;

import java.time.OffsetDateTime;

public class ApiErrorResponse<T> {
	private boolean success;
	private T data;
	private String message;
	private String code;
	private String path;
	private String timestamp;

	public ApiErrorResponse() {
	}

	public ApiErrorResponse(boolean success, T data, String message) {
		this.success = success;
		this.data = data;
		this.message = message;
	}
	
	public ApiErrorResponse(String code, String message, String path) {
		
		this.success = false;
		this.code = code;
		this.message = message;
		this.path = path;
		this.timestamp = OffsetDateTime.now().toString();
	}

	public static <T> ApiErrorResponse<T> ok(T data, String message) {
		return new ApiErrorResponse<>(true, data, message);
	}

	public static <T> ApiErrorResponse<T> created(T data, String message) {
		return new ApiErrorResponse<>(true, data, message);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}