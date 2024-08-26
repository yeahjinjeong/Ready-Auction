package com.readyauction.app.user.entity;

import com.readyauction.app.user.dto.ProfileDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("mem") // Member타입 구분하는 값을 mem로 지정 (기본값: Member)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
public class Member extends User {
    private Integer mannerScore; // 매너지수

    public void setDefaultAuthorities() {
        this.setAuthorities(Set.of(Authority.ROLE_USER));
    }

    public void changeName(String name) {
        this.setName(name);
    }

    public Member(String nickname, String address, String profilePicture, Integer mannerScore) {
        super(nickname, address, profilePicture);  // User 필드 초기화
        this.mannerScore = mannerScore;
    }

    public void updateMannerScore(Integer initialScore) {
        this.mannerScore = initialScore;
    }

    public void increaseMannerScore() {
        this.mannerScore += 5;
    }

    public void decreaseMannerScore() {
        this.mannerScore -= 5;
    }
}