package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>{
	Ingredient findByIngredientName(String ingredientName);
	
}
