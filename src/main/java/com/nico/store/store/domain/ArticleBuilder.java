package com.nico.store.store.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArticleBuilder {
		
	private String title;
	private String description;
	private int stock;	
	private double price;
	private Set<String> pictures;
	private List<String> sizes;
	private List<String> categories;
	private List<String> brands;
	
	public ArticleBuilder() {
	}
	
	public ArticleBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public ArticleBuilder withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public ArticleBuilder stockAvailable(int stock) {
		this.stock = stock;
		return this;
	}
	
	public ArticleBuilder withPrice(double price) {
		this.price = price;
		return this;
	}
	
	public ArticleBuilder imageLink(Set<String> pictures) {
		this.pictures = pictures;
		return this;
	}
	
	public ArticleBuilder sizesAvailable(List<String> sizes) {
		this.sizes = sizes;
		return this;
	}
	
	public ArticleBuilder ofCategories(List<String> categories) {
		this.categories = categories;
		return this;
	}
	
	public ArticleBuilder ofBrand(List<String> brands) {
		this.brands = brands;
		return this;
	}
	
	public Article build() {
		Article article = new Article();
		article.setTitle(this.title);
		article.setDescription(this.description);
		article.setPrice(this.price);
		article.setStock(this.stock);
		article.setPicture(this.pictures);		
		
		if (this.sizes != null && !this.sizes.isEmpty()) {
			Set<Size> sizeElements = new HashSet<>();
			for (String val : this.sizes) {
				sizeElements.add(new Size(val,article));
			}	
			article.setSizes(sizeElements);
		}
		
		if (this.categories != null && !this.categories.isEmpty() ) {
			Set<Category> catElements = new HashSet<>();
			for (String val : this.categories) {
				catElements.add(new Category(val,article));
			}
			article.setCategories(catElements);
		}		
		if (this.brands != null && !this.brands.isEmpty() ) {
			Set<Brand> brandlements = new HashSet<>();
			for (String val : this.brands) {
				brandlements.add(new Brand(val,article));
			}
			article.setBrands(brandlements);
		}

		return article;
	}
	
}