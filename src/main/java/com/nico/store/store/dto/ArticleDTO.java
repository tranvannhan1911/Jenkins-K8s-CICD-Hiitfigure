package com.nico.store.store.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ArticleDTO {

	private Long id;
	private String title;
	private String description;
	private Integer stock;
	private Double price;
	private String picture;
	private Set<String> pictures;
	private Set<String> oldPictureUrls;
	private Set<String> sizes;
	private Set<String> brands;
	private Set<String> categories;
}
