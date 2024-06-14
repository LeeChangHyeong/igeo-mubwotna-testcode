package com.sparta.igeomubwotna.service;


import com.sparta.igeomubwotna.dto.RecipeRequestDto;
import com.sparta.igeomubwotna.dto.RecipeResponseDto;
import com.sparta.igeomubwotna.entity.Recipe;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional
    public ResponseEntity saveRecipe(RecipeRequestDto requestDto, User user) {
        Recipe recipe = recipeRepository.save(new Recipe(requestDto, user));

        return ResponseEntity.status(HttpStatus.CREATED).body(new RecipeResponseDto(recipe));
    }

    public ResponseEntity getRecipe(Long recipeId) {
        Recipe recipe = findById(recipeId);

        return ResponseEntity.status(HttpStatus.OK).body(new RecipeResponseDto(recipe));
    }

    @Transactional
    public ResponseEntity editRecipe(Long recipeId, RecipeRequestDto requestDto, User user) {
        Recipe recipe = findById(recipeId);
        checkUserSame(recipe, user);
        recipe.update(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(new RecipeResponseDto(recipe));

    }

    @Transactional
    public ResponseEntity deleteRecipe(Long recipeId, User user) {
        Recipe recipe = findById(recipeId);
        checkUserSame(recipe, user);
        recipeRepository.delete(recipe);

        return ResponseEntity.status(HttpStatus.OK).body((recipeId + " 번 삭제 완료"));
    }

    public ResponseEntity getAllRecipe(int page, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Recipe> recipeList = recipeRepository.findAll(pageable);

        if (recipeList.getTotalElements() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body("먼저 작성하여 소식을 알려보세요!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(recipeList.map(RecipeResponseDto::new));
    }

    public ResponseEntity getDateRecipe(int page, String startDate, String endDate) {

        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0, 0);
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(23, 59, 59);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<Recipe> recipeList = recipeRepository.findAllByCreatedAtBetween(pageable, startDateTime, endDateTime);

        if (recipeList.getTotalElements() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body("먼저 작성하여 소식을 알려보세요!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(recipeList.map(RecipeResponseDto::new));
    }

    private void checkUserSame(Recipe recipe, User user) {
        if (!(recipe.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("작성자만 접근할 수 있습니다.");
        }
    }

    public Recipe findById(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() ->
                new IllegalArgumentException("해당 레시피가 존재하지 않습니다.")
        );
    }

}
