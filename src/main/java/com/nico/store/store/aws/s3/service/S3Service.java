package com.nico.store.store.aws.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	@Value("${aws.s3.bucket.name}")
	private String bucketName;

	@Autowired
	private AmazonS3 s3Client;

	public String uploadFile(MultipartFile file) {
		File fileObj = convertMultiPartFileToFile(file);
		String rand = new Random().ints(48, 57).limit(10)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		String extend = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String fileName = System.currentTimeMillis() + rand + "." + extend;

		s3Client.putObject(
				new PutObjectRequest(bucketName, fileName, fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
		fileObj.delete();
		return "https://" + bucketName + ".s3.ap-southeast-1.amazonaws.com/" + fileName;
	}

	public Set<String> uploadFiles(MultipartFile[] files) {
		Set<String> fileUploaded = new HashSet<String>();

		for (MultipartFile file : files) {
			fileUploaded.add(this.uploadFile(file));
		}

		return fileUploaded;
	}

	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convertedFile;
	}
}
