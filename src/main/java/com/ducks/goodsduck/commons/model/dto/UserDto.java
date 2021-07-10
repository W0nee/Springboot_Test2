package com.ducks.goodsduck.commons.model.dto;

import com.ducks.goodsduck.commons.model.entity.User;
import com.ducks.goodsduck.commons.model.enums.SocialType;
import com.ducks.goodsduck.commons.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDto {

    private String socialAccountId;
    private String nickName;
    private String phoneNumber;
    private String email;
    private String imageUrl;
    private String jwt;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserDto() {
    }

    public UserDto(User user) {
        this.nickName = user.getNickName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
    }

    public UserDto(User user, String socialAccountId, String jwt) {
        this.nickName = user.getNickName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
        this.socialAccountId = socialAccountId;
        this.jwt = jwt;
    }
}
