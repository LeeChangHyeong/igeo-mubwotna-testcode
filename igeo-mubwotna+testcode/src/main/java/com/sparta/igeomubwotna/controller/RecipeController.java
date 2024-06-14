package com.sparta.igeomubwotna.controller;

import com.sparta.igeomubwotna.dto.RecipeRequestDto;
import com.sparta.igeomubwotna.dto.RecipeResponseDto;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/")
    public ResponseEntity saveRecipe(@Valid @RequestBody RecipeRequestDto requestDto,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
        }
        return recipeService.saveRecipe(requestDto, userDetails.getUser());

    }

    @GetMapping("/{recipeId}")
    public ResponseEntity getRecipe(@PathVariable Long recipeId) {
        return recipeService.getRecipe(recipeId);
    }

    @PatchMapping("/{recipeId}")
    public ResponseEntity<RecipeResponseDto> editRecipe(@PathVariable Long recipeId,
                                                        @RequestBody RecipeRequestDto requestDto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return recipeService.editRecipe(recipeId, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity deleteRecipe(@PathVariable Long recipeId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return recipeService.deleteRecipe(recipeId, userDetails.getUser());
    }

    @GetMapping("/")
    public ResponseEntity getAllRecipe(@RequestParam("page") int page,
                                       @RequestParam(required = false, defaultValue = "createdAt", value = "sortBy") String sortBy) {
        return recipeService.getAllRecipe(page - 1, sortBy);
    }

    @GetMapping("/date/")
    public ResponseEntity getDateRecipe(@RequestParam("page") int page,
                                        @RequestParam("startdate") String startDate,
                                        @RequestParam("enddate") String endDate) {
        return recipeService.getDateRecipe(page - 1, startDate, endDate);
    }

}
