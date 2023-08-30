package com.example.demo.controller;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Love;
import com.example.demo.entity.Recipe;
import com.example.demo.entity.User;
import com.example.demo.formdto.RecipeFormDto;
import com.example.demo.repository.LoveRepository;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.service.LoveService;
import com.example.demo.service.RecipeService;

import jakarta.validation.Valid;

@Controller
public class RecipeController {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeService recipeservice;
	@Autowired
	private LoveRepository loveRepository;
	@Autowired
	private LoveService loveservice;
	
	
	@GetMapping("/recipe1")
	public String listRecipes(Model model, @RequestParam(required = false) String title) {
		List<Recipe> recipes = recipeRepository.findAll();
		model.addAttribute("recipes", recipes);
		return "recipeList2";
	}
	@GetMapping("/recipe")
	public String listRecipes1(Model model, @RequestParam(required = false, defaultValue = "0") int page) {
		int pageSize = 20; // 페이지당 레시피 수
		List<Recipe> recipes = recipeRepository.findAll();
		model.addAttribute("recipes", recipes);
		model.addAttribute("currentPage", page);
		return "recipeList3";
	}
	 @GetMapping("/createRecipe")
	    public String createRecipeForm(Model model) {
	        model.addAttribute("recipe", new Recipe());
	        return "createRecipe3";
	    }
	    
	    @PostMapping("/createRecipe")
	    public String createRecipe(@ModelAttribute Recipe recipe, MultipartFile file) throws Exception{
	    	
	    	recipeservice.write(recipe, file);
	    	recipeRepository.save(recipe);
	    	//model.addAttribute("message", "글작성이 완료되었습니다.");
	    	//model.addAttribute("searchUrl", "redirect:/recipe");
	        return "redirect:/recipe";
	        }
	    
	    @PostMapping("/like")
	    public String like(@RequestParam int recipe_id, @RequestParam String user_id) {
	        Recipe recipe = new Recipe();
	        recipe.setRecipe_id(recipe_id);
	    	
	        User user = new User();
	        user.setUser_id(user_id);
	        
	    	Love love = new Love();
	        love.setUser(user);
	        love.setRecipe(recipe);
	        
	    	loveservice.saveLove(love);
	        
	        return "redirect:/recipe/"+recipe_id;
	    }
	    
	    
	    
	    
	    @GetMapping("/recipe/{recipe_id}")
        public String userRecipeview(@PathVariable("recipe_id") int recipe_id, Model model) {
            Recipe recipe = recipeRepository.findById(recipe_id);
            if (recipe != null) {
                recipeRepository.incrementViewCount(recipe_id); // 조회수 업데이트
                model.addAttribute("recipe", recipe);
                return "userRecipe"; // 레시피 페이지 템플릿
            }
            
            return "userRecipe";
        }
	    
	    @GetMapping("/editRecipe/{recipe_id}")
        public String editRecipeForm(@PathVariable Integer recipe_id, Model model) {
            Recipe recipe = recipeRepository.findById(recipe_id).orElseThrow(() -> new IllegalArgumentException("Invalid Recipe ID: " + recipe_id));
            model.addAttribute("recipe", recipe);
            return "editRecipe";
        }

        @PostMapping("/editRecipe/{recipe_id}")
        public String editRecipe(@PathVariable Long recipe_id, @ModelAttribute Recipe recipe) {
            recipeRepository.save(recipe);
            return "redirect:/recipe";
        }
        
        @GetMapping("/deleteRecipe/{recipe_id}")
        public String deleteRecipe(@PathVariable Integer recipe_id) {
            recipeRepository.deleteById(recipe_id);
            return "redirect:/recipe";
        }
        
       
        
        
        
        
        
        
        
        @GetMapping("/viewRecipe")
    	public String viewRecipe(Model model) {
    		List<Recipe> recipes = recipeRepository.findAll();
    		model.addAttribute("recipes", recipes);
    		return "viewRecipe";
    	}
        
	}