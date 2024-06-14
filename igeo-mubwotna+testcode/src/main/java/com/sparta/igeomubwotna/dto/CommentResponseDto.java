package com.sparta.igeomubwotna.dto;

import com.sparta.igeomubwotna.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private long id;
    private String content;
    private String userId;
    private LocalDateTime createdAt;
    private Long likeCount;

    public CommentResponseDto(Long id, String content, String userId, LocalDateTime createdAt, Long likeCount) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getUserId(),
                comment.getCreatedAt(),
                comment.getLikeCount()
        );
    }
}
