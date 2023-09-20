package com.example.demo.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

	@Query("SELECT r.title FROM Recipe r WHERE r.recipe_id = :recipeId")
    String findTitleByRecipeId(Long recipeId);
	@Query("SELECT r.main_photo FROM Recipe r WHERE r.recipe_id = :recipeId")
    String findMainPhotoByRecipeId(Long recipeId);

	List<Recipe> findByCategoryNameOrderByTotalLoveDesc(String categoryName);
	List<Recipe> findByCategoryNameOrderByDailyLoveDesc(String categoryName);
	List<Recipe> findByCategoryNameOrderByWeeklyLoveDesc(String categoryName);
	List<Recipe> findByCategoryNameOrderByMonthlyLoveDesc(String categoryName);
	List<Recipe> findTop10ByOrderByTotalLoveDesc();

	List<Recipe> findByOrderByTotalLoveDesc();

	Recipe findById(int recipe_id);
	
	@Transactional
	@Modifying
    @Query("UPDATE Recipe r SET r.view_count = r.view_count + 1 WHERE r.recipe_id = :recipe_id")
    void incrementViewCount(@Param("recipe_id") int recipe_id);
	
	@Transactional
	@Modifying
    @Query("UPDATE Recipe r SET r.totalLove = :totalLove WHERE r.recipe_id = :recipe_id")
	void updateTotalLoves(@Param("recipe_id") int recipe_id, @Param("totalLove") int totalLove);
	
	@Transactional
    @Modifying
    @Query("UPDATE Recipe r SET r.totalLove = (SELECT COUNT(u.activityId) FROM Love u)")
    void updateTotalFromActivity();
	
	@Transactional
    @Modifying
    @Query("UPDATE Recipe r SET r.dailyLove = :dailyLove, r.weeklyLove = :weeklyLove, r.monthlyLove = :monthlyLove WHERE r.recipe_id = :recipe_id")
    void updatePeriodLikes(@Param("recipe_id") int recipe_id, @Param("dailyLove") int dailyLove, @Param("weeklyLove") int weeklyLove, @Param("monthlyLove") int monthlyLove);

	List<Recipe> findByOrderByDailyLoveDesc();
	List<Recipe> findByOrderByWeeklyLoveDesc();
	List<Recipe> findByOrderByMonthlyLoveDesc();
	


}
	