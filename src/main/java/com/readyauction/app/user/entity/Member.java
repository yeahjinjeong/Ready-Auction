package com.readyauction.app.user.entity;

import com.readyauction.app.user.dto.ProfileDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("mem") // Member타입 구분하는 값을 mem로 지정 (기본값: Member)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
public class Member extends User {

    @Column
    private LocalDate birth;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String address;

    @Column
    private String profilePicture;

    private Integer mannerScore; // 매너지수

    @Builder
    public Member(
            final String email,
            final String password,
            final String name,
            final LocalDate birth,
            final Gender gender,
            final String phone,
            final String nickname,
            final String address,
            final String profilePicture,
            final Integer mannerScore,
            final UserStatus userStatus
    ) {
        super(email, password, name, phone, userStatus, Set.of(Authority.ROLE_USER));
        this.birth = birth;
        this.gender = gender;
        this.nickname = nickname;
        this.address = address;
        this.profilePicture = profilePicture;
        this.mannerScore = mannerScore;
    }

    public void changeName(String name) {
        super.changeName(name);
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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

    public void chageUserStatus(UserStatus userStatus) {
        super.changeUserStatus(userStatus);
    }
}