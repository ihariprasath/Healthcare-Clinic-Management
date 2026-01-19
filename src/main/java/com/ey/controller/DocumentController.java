
package com.ey.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.entity.MedicalDocument;
import com.ey.exception.ApiResponse;
import com.ey.service.DocumentService;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

	private final DocumentService service;

	public DocumentController(DocumentService service) {
		this.service = service;
	}

	@PostMapping("/patients/{id}/documents")
	public ResponseEntity<ApiResponse<MedicalDocument>> upload(@PathVariable Long id, @RequestParam String type,
			@RequestParam MultipartFile file) throws Exception {

		return ResponseEntity.status(201)
				.body(ApiResponse.created(service.upload(id, type, file), "Document uploaded"));
	}

	@GetMapping("/patients/{id}/documents")
	public ResponseEntity<ApiResponse<List<MedicalDocument>>> list(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.ok(service.list(id), "Documents list"));
	}

	@GetMapping("/documents/{docId}/download")
	public ResponseEntity<Resource> download(@PathVariable Long docId) {
		Resource r = service.download(docId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document")
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(r);
	}
}