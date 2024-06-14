package com.sparta.igeomubwotna.service;

import com.sparta.igeomubwotna.dto.CommentRequestDto;
import com.sparta.igeomubwotna.dto.CommentResponseDto;
import com.sparta.igeomubwotna.entity.Comment;
import com.sparta.igeomubwotna.entity.Recipe;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeService recipeService;
    private final UserService userService;

    /* Create : 댓글 작성 */
    public ResponseEntity createComment(CommentRequestDto requestDto, Long recipeId, User user) {
        Recipe recipe = recipeService.findById(recipeId);
        Comment comment = new Comment(requestDto, recipe, user);

        commentRepository.save(comment);
        return ResponseEntity.ok("comment가 등록되었습니다.");
    }

    /* Read : 댓글 조회 (레시피에 대한 전체 댓글) */
    public List<CommentResponseDto> getComment(Long recipeId) {

        List<Comment> commentList = commentRepository.findByRecipeId(recipeId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(CommentResponseDto.toDto(comment));
        }
        return commentResponseDtoList;
    }

    /* Update : 댓글 수정 */
    @Transactional
    public ResponseEntity updateComment(Long recipeId, Long commentId, CommentRequestDto requestDto, User user) {
        Recipe recipe = recipeService.findById(recipeId);
        Comment comment = findById(commentId);

        checkUser(user, comment, recipe);

        comment.update(requestDto.getContent());
        return ResponseEntity.ok("comment가 수정되었습니다.");
    }

    @Transactional
    /* Delete : 댓글 삭제 */
    public void deleteComment(Long recipeId, Long commentId, User user) {
        Recipe recipe = recipeService.findById(recipeId);
        Comment comment = findById(commentId);

        checkUser(user, comment, recipe);

        commentRepository.delete(comment);
    }


    /* ID로 comment 찾기 */
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );
    }

    /* 예외처리 */
    private void checkUser(User user, Comment comment, Recipe recipe) {
        // 게시글과 댓글의 관계가 없을 경우
        if (recipe.getId() != comment.getRecipe().getId()) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }
        // User가 다른 경우
        if (!(comment.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
    }
}
