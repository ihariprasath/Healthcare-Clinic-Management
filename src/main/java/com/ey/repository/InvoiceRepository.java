package com.ey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
	

}
