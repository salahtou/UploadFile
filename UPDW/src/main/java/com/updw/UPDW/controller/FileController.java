package com.updw.UPDW.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.updw.UPDW.dao.DBFileRepository;
import com.updw.UPDW.model.DBFile;
import com.updw.UPDW.payload.UploadFileResponse;
import com.updw.UPDW.service.DBFileStorageService;

@RestController
@CrossOrigin("*")
public class FileController {

	
	String fileDownloadUri;
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private DBFileStorageService dbFileStorageService;

	@Autowired
	private DBFileRepository repository;

	@GetMapping("/hello")
	public String gethELLO() {
		return "Hello World Test Test";

	}
	
	// Method Upload To BD (Blob)

	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		DBFile dbFile = dbFileStorageService.storeFile2(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(dbFile.getId()).toUriString();
		return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());
	}

	

	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files)  {
		

		return Arrays.asList(files).stream().map(file -> uploadFile2(file)).collect(Collectors.toList());
		
		
		
	}
	

	/*
	 * @GetMapping("/downloadFile/{fileId}") public ResponseEntity<Resource>
	 * downloadFile(@PathVariable String fileId) { // Load file from database DBFile
	 * dbFile = dbFileStorageService.getFile(fileId);
	 * 
	 * return
	 * ResponseEntity.ok().contentType(MediaType.parseMediaType(dbFile.getFileType()
	 * )) .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" +
	 * dbFile.getFileName() + "\"") .body(new ByteArrayResource(dbFile.getData()));
	 * }
	 */
	

	@PostMapping("/uploadD")
	public UploadFileResponse uploadFile2(@RequestParam("file") MultipartFile file)  {
		
		DBFile dbFile = dbFileStorageService.storeFile2(file);

		if (file.getContentType().equals("application/pdf")) {

			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/FilePDF/")
					.path(dbFile.getId()).toUriString();
		}

		if (file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {

			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/FileXSLX/")
					.path(dbFile.getId()).toUriString();
		}

		if (file.getContentType().equals("application/vnd.ms-excel")) {

			fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/FileCSV/")
					.path(dbFile.getId()).toUriString();
		}

		// DBFile db = new DBFile();	

		// repository.save(db);
		return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());

	}

	@GetMapping(path = "/FilePDF/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] getPDF(@PathVariable("id") String id) throws Exception {

		DBFile d = repository.findById(id).get();

		return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/files/" + d.getFileName()));

	}

	@GetMapping(path = "/FileXSLX/{id}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public byte[] getXSLX(@PathVariable("id") String id) throws Exception {

		DBFile d = repository.findById(id).get();

		return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/files/" + d.getFileName()));

	}

	@GetMapping(path = "/FileCSV/{id}", produces = "application/vnd.ms-excel")
	public byte[] getCSV(@PathVariable("id") String id) throws Exception {

		DBFile d = repository.findById(id).get();

		return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/files/" + d.getFileName()));

	}

	@GetMapping("/getAllFiles")
	public List<DBFile> getAllFiles() {
		return repository.findAll();

	}
	

	
}
