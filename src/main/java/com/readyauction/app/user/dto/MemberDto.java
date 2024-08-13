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

    public static MemberDto toMemberDto(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.setId(member.getId());
        memberDto.setEmail(member.getEmail());
        memberDto.setPassword(member.getPassword());
        memberDto.setName(member.getName());
        memberDto.setPhone(member.getPhone());
        memberDto.setAddress(member.getAddress());
        memberDto.setBirth(member.getBirth());
        memberDto.setNickname(member.getNickname());
        memberDto.setProfilePicture(member.getProfilePicture());
        memberDto.setMannerScore(member.getMannerScore());
        memberDto.setCreatedAt(member.getCreatedAt());
//        memberDto.setUpdatedAt(member.getUpdatedAt());
        memberDto.setUserStatus(member.getUserStatus());
        return memberDto;
    }
}
