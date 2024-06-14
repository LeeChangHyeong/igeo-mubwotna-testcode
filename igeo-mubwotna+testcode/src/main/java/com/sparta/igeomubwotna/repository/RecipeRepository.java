package com.sparta.igeomubwotna.repository;

import com.sparta.igeomubwotna.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findAllByCreatedAtBetween(Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime);
}