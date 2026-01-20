package com.ey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ handles our custom ApiException
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest req) {

        ApiErrorResponse body = new ApiErrorResponse(
                ex.getErrorCode().name(),
                ex.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(body, ex.getStatus());
    }

    // ✅ handles any other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {

        ApiErrorResponse body = new ApiErrorResponse(
                ErrorCode.INTERNAL_ERROR.name(),
                "Something went wrong",
                req.getRequestURI()
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}