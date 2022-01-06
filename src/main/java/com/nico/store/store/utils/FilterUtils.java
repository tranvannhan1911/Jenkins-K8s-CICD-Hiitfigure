package com.nico.store.store.utils;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 1/2/2022 10:38 AM
 */
public class FilterUtils {
	private FilterUtils() {
		throw new IllegalStateException(this.getClass().getSimpleName());
	}

	public static Set<MultipartFile> multipartFilesFilter(Set<MultipartFile> multipartFiles) {
		return multipartFiles.stream()
				.filter(p -> !Objects.equals(p.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE))
				.collect(Collectors.toSet());
	}
}
