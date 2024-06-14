package com.sparta.igeomubwotna.service;

import com.sparta.igeomubwotna.entity.*;
import com.sparta.igeomubwotna.repository.CommentLikesRepository;
import com.sparta.igeomubwotna.repository.RecipeLikesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final RecipeLikesRepository recipeLikesRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final UserService userService;
    private final RecipeService recipeService;
    private final CommentService commentService;

    @Transactional
    public ResponseEntity addRecipeLike(Long recipeId, User user) {

        User foundUser = userService.findById(user.getId());

        Recipe foundRecipe = recipeService.findById(recipeId);

        if (foundUser.getUserId().equals(foundRecipe.getUser().getUserId())) {
            throw new IllegalArgumentException("자신이 작성한 레시피에는 좋아요를 남길 수 없습니다.");
        }

        var RecipeLikes = new RecipeLikes(foundUser, foundRecipe);

        if (recipeLikesRepository.findByUserAndRecipe(foundUser, foundRecipe).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 레시피입니다.");
        }

        recipeLikesRepository.save(RecipeLikes);

        foundRecipe.addLike();

        return ResponseEntity.status(200).body("좋아요 성공!");
    }

    @Transactional
    public ResponseEntity removeRecipeLike(Long recipeLikeId, User user) {

        RecipeLikes foundlike = recipeLikesRepository.findById(recipeLikeId).orElseThrow(
                () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));

        Recipe foundRecipe = recipeService.findById(foundlike.getRecipe().getId());

        if (!(user.getUserId().equals(foundlike.getUser().getUserId()))) {
            throw new IllegalArgumentException("다른 사람의 좋아요는 삭제할 수 없습니다.");
        }

        recipeLikesRepository.delete(foundlike);

        foundRecipe.minusLike();

        return ResponseEntity.status(200).body("좋아요 취소 성공!");
    }

    @Transactional
    public ResponseEntity addCommentLike(Long commentId, User user) {

        User foundUser = userService.findById(user.getId());

        Comment foundComment = commentService.findById(commentId);

        if (foundUser.getUserId().equals(foundComment.getUser().getUserId())) {
            throw new IllegalArgumentException("자신이 작성한 댓글에는 좋아요를 남길 수 없습니다.");
        }

        if (commentLikesRepository.findByUserAndComment(foundUser, foundComment).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
        }

        var CommentLikes = new CommentLikes(foundUser, foundComment);

        commentLikesRepository.save(CommentLikes);

        foundComment.addLike();

        return ResponseEntity.status(200).body("좋아요 성공!");
    }

    @Transactional
    public ResponseEntity removeCommentLike(Long commentLikeId, User user) {

        CommentLikes foundLike = commentLikesRepository.findById(commentLikeId).orElseThrow(
                () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));

        Comment foundComment = commentService.findById(foundLike.getComment().getId());

        if (!(user.getUserId().equals(foundLike.getUser().getUserId()))) {
            throw new IllegalArgumentException("다른 사람의 좋아요는 삭제할 수 없습니다.");
        }

        commentLikesRepository.delete(foundLike);

        foundComment.minusLike();

        return ResponseEntity.status(200).body("좋아요 취소 성공!");
    }
}
