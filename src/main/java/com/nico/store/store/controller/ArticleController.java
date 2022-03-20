package com.nico.store.store.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nico.store.store.aws.s3.service.S3Service;
import com.nico.store.store.domain.Article;
import com.nico.store.store.domain.ArticleBuilder;
import com.nico.store.store.domain.Brand;
import com.nico.store.store.domain.Category;
import com.nico.store.store.domain.Size;
import com.nico.store.store.service.ArticleService;


@Controller
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private S3Service s3Service;

	@RequestMapping("/add")
	public String addArticle(Model model) {
		Article article = new Article();
		model.addAttribute("article", article);
		model.addAttribute("allSizes", articleService.getAllSizes());
		model.addAttribute("allBrands", articleService.getAllBrands());
		model.addAttribute("allCategories", articleService.getAllCategories());
		return "addArticle";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addArticlePost(@ModelAttribute("article") Article article,
			@RequestParam(value = "files") MultipartFile[] files, HttpServletRequest request) {
		
		if (files.length == 0 || (files.length == 1 && files[0].getSize() == 0) || files.length > 5) {
			return "redirect:error";
		}

		for (MultipartFile file : files) {
			String contentType = file.getContentType();
			if (!contentType.startsWith("image/")) {
				return "redirect:error";
			}
		}

		Set<String> fileUploaded = s3Service.uploadFiles(files);

		Article newArticle = new ArticleBuilder().withTitle(article.getTitle())
				.withDescription(article.getDescription()).stockAvailable(article.getStock())
				.withPrice(article.getPrice()).withDiscount(article.getDiscount())
				.imageLink(fileUploaded)
				.sizesAvailable(Arrays.asList(request.getParameter("size").split("\\s*,\\s*")))
				.ofCategories(Arrays.asList(request.getParameter("category").split("\\s*,\\s*")))
				.ofBrand(Arrays.asList(request.getParameter("brand").split("\\s*,\\s*"))).build();
		articleService.saveArticle(newArticle);
		return "redirect:article-list";
	}

	@RequestMapping("/article-list")
	public String articleList(Model model) {
		List<Article> articles = articleService.findAllArticles();
		model.addAttribute("articles", articles);
		return "articleList";
	}

	@RequestMapping("/edit")
	public String editArticle(@RequestParam("id") Long id, Model model) {
		Article article = articleService.findArticleById(id);
		String preselectedSizes = "";
		for (Size size : article.getSizes()) {
			preselectedSizes += (size.getValue() + ",");
		}
		String preselectedBrands = "";
		for (Brand brand : article.getBrands()) {
			preselectedBrands += (brand.getName() + ",");
		}
		String preselectedCategories = "";
		for (Category category : article.getCategories()) {
			preselectedCategories += (category.getName() + ",");
		}
		model.addAttribute("article", article);
		model.addAttribute("preselectedSizes", preselectedSizes);
		model.addAttribute("preselectedBrands", preselectedBrands);
		model.addAttribute("preselectedCategories", preselectedCategories);
		model.addAttribute("allSizes", articleService.getAllSizes());
		model.addAttribute("allBrands", articleService.getAllBrands());
		model.addAttribute("allCategories", articleService.getAllCategories());
		return "editArticle";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editArticlePost(@ModelAttribute("article") Article article,
			@RequestParam(value = "files") MultipartFile[] files, HttpServletRequest request) {
		List<String> oldPictureUrl = Arrays.asList(request.getParameter("old_picture_url").split("\\s*,\\s*"));
		int old_img = 0;
		if(oldPictureUrl.size() != 0 && oldPictureUrl.get(0).equals("")) {
			oldPictureUrl.clear();
		}
		
		old_img = oldPictureUrl.size(); 
		
		int img_upload = 0;
		if(files.length != 0 && files[0].getSize() != 0)
			img_upload = files.length; 

		if (old_img + img_upload == 0) {
			return "redirect:error";
		}

		if (old_img + img_upload > 5) {
			return "redirect:error";
		}
		
		Set<String> fileUploaded = new HashSet<String>();
		
		if(img_upload > 0) {
			for (MultipartFile file : files) {
				String contentType = file.getContentType();
				if (!contentType.startsWith("image/")) {
					return "redirect:error";
				}
			}
			fileUploaded = s3Service.uploadFiles(files);
		}
		
		for (String pictureUrl: oldPictureUrl) {
			fileUploaded.add(pictureUrl);
		}

		Article newArticle = new ArticleBuilder().withTitle(article.getTitle())
				.withDescription(article.getDescription()).stockAvailable(article.getStock())
				.withPrice(article.getPrice()).withDiscount(article.getDiscount())
				.imageLink(fileUploaded)
				.sizesAvailable(Arrays.asList(request.getParameter("size").split("\\s*,\\s*")))
				.ofCategories(Arrays.asList(request.getParameter("category").split("\\s*,\\s*")))
				.ofBrand(Arrays.asList(request.getParameter("brand").split("\\s*,\\s*"))).build();
		newArticle.setId(article.getId());
		articleService.saveArticle(newArticle);
		return "redirect:article-list";
	}

	@RequestMapping("/delete")
	public String deleteArticle(@RequestParam("id") Long id) {
		articleService.deleteArticleById(id);
		return "redirect:article-list";
	}

}
