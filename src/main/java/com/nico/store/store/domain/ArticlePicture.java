package com.nico.store.store.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pictures")
public class ArticlePicture {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="article_id")
	private Article article;
	
	private String pictureUrl;
	
	public ArticlePicture() {
	
	}

	public ArticlePicture(Article article, String picture) {
		super();
		this.article = article;
		this.pictureUrl = picture;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String picture) {
		this.pictureUrl = picture;
	}
	
	
}
