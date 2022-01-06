package com.nico.store.store.utils;

import com.nico.store.store.domain.*;
import com.nico.store.store.dto.ArticleDTO;
import com.nico.store.store.dto.UserDTO;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 1/2/2022 1:27 AM
 */
public class ObjectMapper {

	private ObjectMapper() {
		throw new IllegalStateException(this.getClass().getSimpleName());
	}

	public static <T> T toDTO(Object obj, Class<T> clazz) {
		if (clazz == ArticleDTO.class) {
			return clazz.cast(toArticleDTO(obj));
		} else if (clazz == UserDTO.class) {
			return clazz.cast(toUserDTO(obj));
		} else {
			return null;
		}
	}

	private static ArticleDTO toArticleDTO(Object obj) {
		Article article = (Article) obj;
		return ArticleDTO.builder()
				.id(null == article.getId() ? null : article.getId())
				.title(article.getTitle())
				.description(article.getDescription())
				.price(article.getPrice())
				.stock(article.getStock())
				.sizes(SetUtils.toSetString(article.getSizes(), Size.class))
				.oldPictureUrls(SetUtils.toSetString(article.getPictures(), ArticlePicture.class))
				.brands(SetUtils.toSetString(article.getBrands(), Brand.class))
				.categories(SetUtils.toSetString(article.getCategories(), Category.class))
				.build();
	}

	private static UserDTO toUserDTO(Object obj) {
		User user = (User) obj;
		// TODO: implement UserDTO
		return null == user ? null : new UserDTO();
	}
}
