package com.sparta.igeomubwotna.entity;

import com.sparta.igeomubwotna.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {

    /* Column */
    //ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //내용
    @Column(nullable = false)
    private String content;

    //좋아요 수
    @Column
    private Long likeCount = 0L;

    /* Mapping */
    //작성자 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //레시피 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public void addLike() {
        this.likeCount = likeCount + 1L;
    }

    public void minusLike() {
        this.likeCount = likeCount - 1L;
    }

    /* Constructor */
    public Comment(CommentRequestDto requestDto, Recipe recipe, User user) {
        this.content = requestDto.getContent();
        this.recipe = recipe;
        this.likeCount = 0L;
        this.user = user;
    }

    /* comment 수정 메서드 */
    public void update(String content) {
        this.content = content;
    }
}
