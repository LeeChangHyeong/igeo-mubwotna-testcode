package com.sparta.igeomubwotna.dto;

import com.sparta.igeomubwotna.entity.User;
import lombok.Getter;

@Getter
public class UserProfileDto {
    private String userId;
    private String name;
    private String description;
    private String email;

    public UserProfileDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.description = user.getDescription();
        this.email = user.getEmail();
    }
}