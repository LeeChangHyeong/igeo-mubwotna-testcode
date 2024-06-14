package com.sparta.igeomubwotna.repository;

import com.sparta.igeomubwotna.entity.Comment;
import com.sparta.igeomubwotna.entity.CommentLikes;
import com.sparta.igeomubwotna.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    Optional<CommentLikes> findByUserAndComment(User foundUser, Comment foundComment);
}
