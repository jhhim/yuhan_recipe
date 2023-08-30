package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Love;
import com.example.demo.entity.Recipe;
import com.example.demo.entity.User;

public interface LoveRepository extends JpaRepository<Love, Long>{
    List<Love> findByOrderByActivityId();
    
    
    @Query("SELECT COUNT(l) FROM Love l WHERE l.recipe.recipe_id = :recipe_id")
    int countLovesByRecipeId(@Param("recipe_id") int recipe_id);
    
	//int countLovesByRecipe_id(int recipeId);
    
    //Optional<Love> findByRecipeAndUser(Recipe recipe, User user);
    
}