package com.sparta.igeomubwotna.controller;

import com.sparta.igeomubwotna.dto.CommentRequestDto;
import com.sparta.igeomubwotna.dto.CommentResponseDto;
import com.sparta.igeomubwotna.dto.Response;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe/{recipeId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /* Create */
    @PostMapping
    public ResponseEntity createComment(@RequestBody CommentRequestDto requestDto,
                                        @PathVariable Long recipeId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(requestDto, recipeId, userDetails.getUser());
    }

    /* Read */
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable Long recipeId) {
        return ResponseEntity.ok().body(commentService.getComment(recipeId));
    }

    /* Update */
    @PatchMapping("/{commentId}")
    public ResponseEntity updateComment(@PathVariable Long recipeId,
                                        @PathVariable Long commentId,
                                        @RequestBody CommentRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return (commentService.updateComment(recipeId, commentId, requestDto, userDetails.getUser()));
    }

    /* Delete */
    @DeleteMapping("/{commentId}")
    public ResponseEntity delete(@PathVariable Long recipeId,
                                 @PathVariable Long commentId,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(recipeId, commentId, userDetails.getUser());

        Response response = new Response(HttpStatus.OK.value(), "댓글이 삭제되었습니다");
        return ResponseEntity.ok().body(response);
    }
}
