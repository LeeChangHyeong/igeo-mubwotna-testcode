package com.sparta.igeomubwotna.controller;

import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/recipe/{recipeId}/like")
    public ResponseEntity addRecipeLike(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.addRecipeLike(recipeId, userDetails.getUser());
    }

    @DeleteMapping("/api/recipe/{recipeLikeId}/like")
    public ResponseEntity removeRecipeLike(@PathVariable Long recipeLikeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.removeRecipeLike(recipeLikeId, userDetails.getUser());
    }

    @PostMapping("/api/comment/{commentId}/like")
    public ResponseEntity addCommentLike(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.addCommentLike(commentId, userDetails.getUser());
    }

    @DeleteMapping("/api/comment/{commentLikeId}/like")
    public ResponseEntity removeCommentLike(@PathVariable Long commentLikeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.removeCommentLike(commentLikeId, userDetails.getUser());
    }
}
