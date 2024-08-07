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


    public static MemberDto toMemberDTO(Member memberEntity) {
        MemberDto memberDTO = new MemberDto();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setBirth(memberDTO.getBirth());
        memberDTO.setNickname(memberEntity.getNickname());
        memberDTO.setProfilePicture(memberEntity.getProfilePicture());
        memberDTO.setMannerScore(memberEntity.getMannerScore());
        memberDTO.setCreatedAt(memberEntity.getCreatedAt());
//        memberDTO.setUpdatedAt(memberEntity.getUpdatedAt());
        memberDTO.setUserStatus(memberEntity.getUserStatus());
        return memberDTO;
    }
}
