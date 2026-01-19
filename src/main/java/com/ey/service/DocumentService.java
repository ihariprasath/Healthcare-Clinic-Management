
package com.ey.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ey.entity.MedicalDocument;
import com.ey.repository.MedicalDocumentRepository;

@Service
public class DocumentService {

	@Value("${storage.upload-dir}")
	private String uploadDir;

	private final MedicalDocumentRepository repo;

	public DocumentService(MedicalDocumentRepository repo) {
		this.repo = repo;
	}

	public MedicalDocument upload(Long patientId, String type, MultipartFile file) throws Exception {
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String unique = UUID.randomUUID() + "_" + file.getOriginalFilename();
		File dest = new File(dir, unique);
		Files.copy(file.getInputStream(), dest.toPath());

		MedicalDocument d = new MedicalDocument();
		d.setPatientId(patientId);
		d.setType(type);
		d.setFileName(file.getOriginalFilename());
		d.setStoredPath(dest.getAbsolutePath());

		return repo.save(d);
	}

	public List<MedicalDocument> list(Long patientId) {
		return repo.findByPatientId(patientId);
	}

	public Resource download(Long docId) {
		MedicalDocument d = repo.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));
		return new FileSystemResource(d.getStoredPath());
	}
}