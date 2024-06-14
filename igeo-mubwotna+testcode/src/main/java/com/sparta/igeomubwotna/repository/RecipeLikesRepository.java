package com.sparta.igeomubwotna.repository;

import com.sparta.igeomubwotna.entity.Recipe;
import com.sparta.igeomubwotna.entity.RecipeLikes;
import com.sparta.igeomubwotna.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeLikesRepository extends JpaRepository<RecipeLikes, Long> {
    Optional<RecipeLikes> findByUserAndRecipe(User foundUser, Recipe foundRecipe);
}
