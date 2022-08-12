package com.nico.store.store;

import java.util.Random;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 1/1/2022 12:32 PM
 */
public class StoreApplicationConstant {

	public static final Random RANDOM = new Random();

	enum Endpoint {
		ADD_ARTICLE("addArticle"),
		ARTICLE_LIST("articleList"),
		EDIT_ARTICLE("editArticle");

		Endpoint(String name) {
		}
	}

	enum RedirectEndpoint {
		REDIRECT("redirect:"),
		REDIRECT_ERROR(REDIRECT + "error"),
		REDIRECT_ARTICLE_LIST(REDIRECT + "article-list");

		RedirectEndpoint(String name) {
		}
	}

	enum Attribute {
		ARTICLE("article"),
		ARTICLES("articles"),
		ALL_CATEGORIES("allCategories"),
		ALL_BRANDS("allBrands"),
		ALL_SIZES("allSizes");

		Attribute(String name) {
		}
	}

}
