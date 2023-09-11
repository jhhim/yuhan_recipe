package com.example.demo.service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Recipe;
import com.example.demo.formdto.RecipePageDto;
import com.example.demo.repository.RecipeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {
	
//	private RecipeRepository reciperepository;
//	private RecipeImgService recipeImgservice;
//	private RecipeimgRepository recipeImgrepository;
//	
//	//레시피등록
//	public int saveRecipe(RecipeFormDto recipeFormDto, List<MultipartFile> fileList) throws Exception {
//
//        // 상품 등록 (1번)
//        Recipe item = recipeFormDto.createRecipe();
//        reciperepository.save(item);
//
//        // 이미지 등록(2번, 순서중요)
//        for (int i = 0; i < fileList.size(); i++) {
//            Recipeimg recipeimg = new Recipeimg();
//            recipeimg.setRecipe(item);
//            if (i == 0) {
//                recipeimg.setRepimgyn("Y");
//            } else{
//                recipeimg.setRepimgyn("N");
//            }
//            recipeImgservice.saveRecipeImg(recipeimg, fileList.get(i));
//        }
//        return item.getRecipe_id();
//
//    }
	@Autowired
	private RecipeRepository reciperepository;
	
	public void write(Recipe recipe, MultipartFile file) throws Exception{
		String proijectpath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";
		UUID uuid = UUID.randomUUID();
		String fileName = uuid + "_" + file.getOriginalFilename();
 		File savefile = new File(proijectpath, fileName);
		file.transferTo(savefile);
		recipe.setMain_photo(fileName);
		recipe.setMain_photo_path("/img/"+fileName);
		//reciperepository.save(recipe);
	}
	
	public List<Recipe> getAllRecipes() {
        return reciperepository.findAll();
    }

    public Recipe getRecipeById(int id) {
        Optional<Recipe> optionalRecipe = Optional.ofNullable(reciperepository.findById(id));
        return optionalRecipe.orElse(null);
    }
    
    @Transactional
    public void deletePostWithImage(int recipe_id) {
        // 1. 게시물 정보 조회
        Recipe recipe = reciperepository.findById(recipe_id);

        if (recipe != null) {
            // 2. 이미지 파일 삭제
            String imagePath = recipe.getMain_photo_path();
            if (imagePath != null) {
            	deleteImage(imagePath);
                }
            }

            // 3. 게시물 및 이미지 정보 삭제
            reciperepository.delete(recipe);
        }
    
    private void deleteImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

}
