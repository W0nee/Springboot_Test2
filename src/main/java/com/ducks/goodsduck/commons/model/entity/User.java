package com.ducks.goodsduck.commons.model.entity;

import com.ducks.goodsduck.commons.model.entity.SocialAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;
    private String nickName;
    private String email;
    private String phoneNumber;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user")
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    public User(String nickName, String email, String phoneNumber) {
        this.nickName = nickName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = LocalDateTime.now();
    }

    public void addSocialAccount(SocialAccount socialAccount) {
        socialAccount.setUser(this);
        socialAccounts.add(socialAccount);
    }
}
