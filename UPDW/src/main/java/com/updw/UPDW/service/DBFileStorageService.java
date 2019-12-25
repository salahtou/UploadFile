package com.updw.UPDW.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.updw.UPDW.dao.DBFileRepository;
import com.updw.UPDW.exception.FileStorageException;
import com.updw.UPDW.exception.MyFileNotFoundException;
import com.updw.UPDW.model.DBFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

@Service

public class DBFileStorageService {

	String pattern = "yyyy-MM-dd-HH-mm-ss";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	String date = simpleDateFormat.format(new Date());

	@Autowired
	private DBFileRepository dbFileRepository;

	/*
	 * public DBFile storeFile(MultipartFile file) { String fileName =
	 * StringUtils.cleanPath(file.getOriginalFilename());
	 * 
	 * // Normalize file name String fileName =
	 * StringUtils.cleanPath(file.getOriginalFilename());
	 * 
	 * try { // Check if the file's name contains invalid characters if
	 * (fileName.contains("..")) { throw new
	 * FileStorageException("Sorry! Filename contains invalid path sequence " +
	 * fileName); }
	 * 
	 * DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());
	 * 
	 * return dbFileRepository.save(dbFile);
	 * 
	 * } catch (IOException ex) { throw new
	 * FileStorageException("Could not store file " + fileName +
	 * ". Please try again!", ex); } }
	 */

	public DBFile storeFile2(MultipartFile file) {

		// Normalize file name
		// String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		// String filename = date +"-"+file.getOriginalFilename();

		String filename = date + "-" + file.getOriginalFilename();

		try {
			// Check if the file's name contains invalid characters
			if (filename.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
			}

			DBFile dbFile = new DBFile(filename, file.getContentType());

			try {
				Files.write(Paths.get(System.getProperty("user.home") + "/files/" + filename), file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return dbFileRepository.save(dbFile);

		} catch (Exception ex) {
			throw new FileStorageException("Could not store file " + filename + ". Please try again!", ex);
		}

	}

	public DBFile getFile(String fileId) {
		return dbFileRepository.findById(fileId)
				.orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
	}
}
