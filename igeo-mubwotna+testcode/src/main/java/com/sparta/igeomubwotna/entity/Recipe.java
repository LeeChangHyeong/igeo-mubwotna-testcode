package com.sparta.igeomubwotna.entity;

import com.sparta.igeomubwotna.dto.RecipeRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "recipe")
@NoArgsConstructor
public class Recipe extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private Long recipeLikes;

    public void addLike() {
        this.recipeLikes = recipeLikes + 1L;
    }

    public void minusLike() {
        this.recipeLikes = recipeLikes - 1L;
    }

    public Recipe(RecipeRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.recipeLikes = 0L;
        this.user = user;
    }

    public void update(RecipeRequestDto requestDto) {

        if (requestDto.getTitle() != null) {
            this.title = requestDto.getTitle();
        }
        if (requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }

    }
}
