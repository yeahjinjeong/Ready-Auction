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


    public static MemberDto toMemberDto(Member memberEntity) {
        MemberDto memberDto = new MemberDto();
        memberDto.setId(memberEntity.getId());
        memberDto.setEmail(memberEntity.getEmail());
        memberDto.setPassword(memberEntity.getPassword());
        memberDto.setName(memberEntity.getName());
        memberDto.setPhone(memberEntity.getPhone());
        memberDto.setAddress(memberEntity.getAddress());
        memberDto.setBirth(memberDto.getBirth());
        memberDto.setNickname(memberEntity.getNickname());
        memberDto.setProfilePicture(memberEntity.getProfilePicture());
        memberDto.setMannerScore(memberEntity.getMannerScore());
        memberDto.setCreatedAt(memberEntity.getCreatedAt());
//        memberDto.setUpdatedAt(memberEntity.getUpdatedAt());
        memberDto.setUserStatus(memberEntity.getUserStatus());
        return memberDto;
    }
}
