
package com.ey.service;

import org.springframework.stereotype.Service;

import com.ey.entity.Invoice;
import com.ey.entity.InvoiceStatus;
import com.ey.repository.InvoiceRepository;

@Service
public class BillingService {

	private final InvoiceRepository repo;

	public BillingService(InvoiceRepository repo) {
		this.repo = repo;
	}

	public Invoice create(Invoice inv) {
		inv.setStatus(InvoiceStatus.UNPAID);
		return repo.save(inv);
	}

	public Invoice get(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
	}

	public Invoice pay(Long id) {
		Invoice inv = get(id);
		if (inv.getStatus() == InvoiceStatus.PAID) {
			throw new RuntimeException("Already paid");
		}
		inv.setStatus(InvoiceStatus.PAID);
		return repo.save(inv);
	}

	public Invoice refund(Long id) {
		Invoice inv = get(id);
		inv.setStatus(InvoiceStatus.REFUNDED);
		return repo.save(inv);
	}
}