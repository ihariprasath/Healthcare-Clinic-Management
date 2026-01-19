
package com.ey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.entity.Invoice;
import com.ey.exception.ApiResponse;
import com.ey.service.BillingService;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {

	private final BillingService service;

	public BillingController(BillingService service) {
		this.service = service;
	}

	@PostMapping("/invoices")
	public ResponseEntity<ApiResponse<Invoice>> createInvoice(@RequestBody Invoice inv) {
		return ResponseEntity.status(201).body(ApiResponse.created(service.create(inv), "Invoice created"));
	}

	@GetMapping("/invoices/{id}")
	public ResponseEntity<ApiResponse<Invoice>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "Invoice fetched"));
	}

	@PostMapping("/invoices/{id}/pay")
	public ResponseEntity<ApiResponse<Invoice>> pay(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.pay(id), "Payment success"));
	}

	@PostMapping("/invoices/{id}/refund")
	public ResponseEntity<ApiResponse<Invoice>> refund(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.refund(id), "Refund success"));
	}

	@GetMapping("/invoices/{id}/pdf")
	public ResponseEntity<ApiResponse<Invoice>> pdf(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.get(id), "PDF skipped - returning JSON"));
	}
}