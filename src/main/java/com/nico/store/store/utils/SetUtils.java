package com.nico.store.store.utils;

import com.nico.store.store.domain.ArticlePicture;
import com.nico.store.store.domain.Brand;
import com.nico.store.store.domain.Category;
import com.nico.store.store.domain.Size;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 1/1/2022 11:39 PM
 */
public class SetUtils {

	private SetUtils() {
		throw new IllegalStateException(this.getClass().getSimpleName());
	}

	public static <T> Set<String> toSetString(Set<?> objects, Class<T> clazz) {
		if (clazz == Size.class) {
			Set<Size> sizes = castSet(objects, Size.class);
			return sizes.stream().map(Size::getValue).collect(Collectors.toSet());
		} else if (clazz == Brand.class) {
			Set<Brand> brands = castSet(objects, Brand.class);
			return brands.stream().map(Brand::getName).collect(Collectors.toSet());
		} else if (clazz == Category.class) {
			Set<Category> categories = castSet(objects, Category.class);
			return categories.stream().map(Category::getName).collect(Collectors.toSet());
		} else if (clazz == ArticlePicture.class) {
			Set<ArticlePicture> pictures = castSet(objects, ArticlePicture.class);
			return pictures.stream().map(ArticlePicture::getPictureUrl).collect(Collectors.toSet());
		} else {
			return Collections.emptySet();
		}
	}

	private static <T> Set<T> castSet(Object obj, Class<T> clazz) {
		Set<T> resultSet = new HashSet<>();
		if (obj instanceof Set) {
			((Set<?>) obj).forEach(o -> resultSet.add(clazz.cast(o)));
			return resultSet;
		}
		return Collections.emptySet();
	}
}
