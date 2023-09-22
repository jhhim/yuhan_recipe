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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Love;
import com.example.demo.entity.Recipe;
import com.example.demo.entity.User;
import com.example.demo.formdto.RecipeFormDto;
import com.example.demo.repository.LoveRepository;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.service.LoveService;
import com.example.demo.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RecipeController {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeService recipeservice;
	@Autowired
	private LoveRepository loverepository;
	@Autowired
	private LoveService loveservice;
	
	private int likesCount=0;
	
	
	@GetMapping("/recipe1")
	public String listRecipes(Model model, @RequestParam(required = false) String title) {
		List<Recipe> recipes = recipeRepository.findAll();
		model.addAttribute("recipes", recipes);
		return "recipeList";
	}
	
	
	
	@GetMapping("/recipe")
	public String listRecipes1(Model model, @RequestParam(required = false, defaultValue = "0") int page,
								@RequestParam(required = false) String categorName) {
		//List<Recipe> categoryName;
		int pageSize = 20; // 페이지당 레시피 수
		 //if(ca)
		
		List<Recipe> recipes = recipeRepository.findAll();
		model.addAttribute("recipes", recipes);
		model.addAttribute("currentPage", page);
		return "recipeList";
	}
	
	@PostMapping("/SearchRecipe")
    public String searchRecipes(Model model, @RequestParam(name = "categoryName", required = false) List<String> categories) {

        if (categories != null && !categories.isEmpty()) {
            // 선택된 용도 (categories)에 따라 레시피를 검색하고 모델에 추가
            List<Recipe> recipes = recipeRepository.findByCategoryNameIn(categories);
            model.addAttribute("recipes", recipes);
        } else {
            // 선택된 용도가 없으면 모든 레시피를 검색
            List<Recipe> recipes = recipeRepository.findAll();
            model.addAttribute("recipes", recipes);
        }

        return "recipeList"; // 검색 결과를 표시할 뷰 이름
    }
	
	 @GetMapping("/createRecipe")
	    public String createRecipeForm(Model model) {
	        model.addAttribute("recipe", new Recipe());
	        return "createRecipe";
	    }
	    
	    @PostMapping("/createRecipe")
	    public String createRecipe(@ModelAttribute Recipe recipe, MultipartFile file, HttpSession session) throws Exception{
	    	String loggedInNickname = (String) session.getAttribute("loggedInNickname");
	    	recipe.setNickname(loggedInNickname);
	    	recipeservice.write(recipe, file);
	    	recipeRepository.save(recipe);
	    	//model.addAttribute("message", "글작성이 완료되었습니다.");
	    	//model.addAttribute("searchUrl", "redirect:/recipe");
	        return "redirect:/recipe";
	        }
	    
//	    @PostMapping("/SearchRecipe")
//	    public String search(@RequestParam String category,)
	    
	    @PostMapping("/like")
	    public String like(@RequestParam int recipe_id, HttpSession session) {
	        String activity = "좋아요";
	    	Recipe recipe = new Recipe();
	        recipe.setRecipe_id(recipe_id);
	    	
	        User user = new User();
	        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
	        user.setUser_id(loggedInUserId);
	        
	    	Love love = new Love();
	        love.setUser(user);
	        love.setRecipe(recipe);
	        love.setActivity(activity);
	        
	    	loveservice.saveLove(love);
	        
	        return "redirect:/recipe/"+recipe_id;
	    }
	    
	    @PostMapping("/increase_likes")
	    @ResponseBody
	    public int increaseLoves(@RequestParam("recipe_id") String recipe_id) {
	        // 게시물 ID에 해당하는 좋아요 수 증가 로직
	        likesCount++;
	        return likesCount;
	    }
	    
	    
	    
	    
	    @GetMapping("/recipe/{recipe_id}")
        public String userRecipeview(@PathVariable("recipe_id") int recipe_id, Model model, HttpSession session) {
            Recipe recipe = recipeRepository.findById(recipe_id);
            if (recipe != null) {
                recipeRepository.incrementViewCount(recipe_id); // 조회수 업데이트
                
                String activity = "조회";
                Recipe recipe1 = new Recipe();
    	        recipe.setRecipe_id(recipe_id);
                
    	        User user = new User();
    	        String loggedInUserId = (String) session.getAttribute("loggedInUserId");
    	        if (loggedInUserId == null) {
    	            // 사용자가 로그인하지 않은 경우 로그인 페이지로 리다이렉트하거나 다른 처리를 수행합니다.
    	            return "redirect:/login"; // 로그인 페이지로 리다이렉트하는 예시
    	        }
    	        else {
    	        user.setUser_id(loggedInUserId);
    	        
    	        Love love = new Love();
    	        love.setUser(user);
    	        love.setRecipe(recipe);
    	        love.setActivity(activity);
    	        
    	    	loveservice.saveLove(love);
    	    	
                model.addAttribute("recipe", recipe);
                model.addAttribute("likesCount",likesCount);
                //model.addAttribute("recipe", new Recipe());
                return "userRecipe"; // 레시피 페이지 템플릿
            }
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
        public String deleteRecipe(@PathVariable int recipe_id) {
            recipeservice.deletePostWithImage(recipe_id);
            return "redirect:/recipe";
        }
        
       
	}

