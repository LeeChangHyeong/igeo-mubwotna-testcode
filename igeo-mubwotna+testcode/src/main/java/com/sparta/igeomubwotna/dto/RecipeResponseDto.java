package com.sparta.igeomubwotna.dto;

import com.sparta.igeomubwotna.entity.Recipe;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecipeResponseDto {
    String title;
    String content;
    String userId;
    Long recipeLikes;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public RecipeResponseDto(Recipe recipe) {
        this.title = recipe.getTitle();
        this.content = recipe.getContent();
        this.userId = recipe.getUser().getUserId();
        this.recipeLikes = recipe.getRecipeLikes();
        this.createdAt = recipe.getCreatedAt();
        this.modifiedAt = recipe.getModifiedAt();
    }
}
