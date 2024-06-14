package com.sparta.igeomubwotna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RecipeRequestDto {

	@NotBlank
	private String title;

	@NotBlank
	private String content;
}
