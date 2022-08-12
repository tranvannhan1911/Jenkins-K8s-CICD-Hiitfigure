package com.nico.store.store.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String description;
	private int stock;
	private double price;
	private int discount;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<ArticlePicture> pictures;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Size> sizes;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Brand> brands;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Category> categories;

	public boolean hasStock(int amount) {
		return (this.getStock() > 0) && (amount <= this.getStock());
	}

	public void decreaseStock(int amount) {
		this.stock -= amount;
	}

	public void increaseStock(int amount){
		this.stock += amount;
	}

	public void addSize(Size size) {
		sizes.add(size);
		size.setArticle(this);
	}

	public void removeSize(Size size) {
		sizes.remove(size);
		size.setArticle(null);
	}

	public void addCategory(Category category) {
		categories.add(category);
		category.setArticle(this);
	}

	public void removeCategory(Category category) {
		categories.remove(category);
		category.setArticle(null);
	}

	public void addSize(Brand brand) {
		brands.add(brand);
		brand.setArticle(this);
	}

	public void removeSize(Brand brand) {
		brands.remove(brand);
		brand.setArticle(null);
	}
}
