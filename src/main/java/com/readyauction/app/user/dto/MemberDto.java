package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Integer mannerScore;
    private Gender gender;
    private LocalDate birth;
    private String nickname;
    private String profilePicture;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private UserStatus userStatus;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.mannerScore = member.getMannerScore();
        this.gender = member.getGender();
        this.birth = member.getBirth();
        this.nickname = member.getNickname();
        this.profilePicture = member.getProfilePicture();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
        this.deletedAt = member.getUpdatedAt();
        this.userStatus = member.getUserStatus();
    }
}
