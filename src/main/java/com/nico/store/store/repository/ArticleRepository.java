package com.nico.store.store.repository;


import java.util.List;
import java.util.Optional;

import com.nico.store.store.domain.CartItem;
import org.springframework.data.jpa.repository.*;

import com.nico.store.store.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
	
	@EntityGraph(attributePaths = { "sizes", "categories", "brands" })
	List<Article> findAllEagerBy();	
		
	@EntityGraph(attributePaths = { "sizes", "categories", "brands" })
	Optional<Article> findById(Long id);
	
	@Query("SELECT DISTINCT s.value FROM Size s")
	List<String> findAllSizes();
	
	@Query("SELECT DISTINCT c.name FROM Category c")
	List<String> findAllCategories();
	
	@Query("SELECT DISTINCT b.name FROM Brand b")
	List<String> findAllBrands();

}
