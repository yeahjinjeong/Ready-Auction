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
public class MemberDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private String image;
    private Integer mannerScore;
    private Gender gender;
    private LocalDate birthday;
    private String nickname;
    private String profilePicture;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private UserStatus userStatus;


    public static MemberDTO toMemberDTO(Member memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setBirthday(memberDTO.getBirthday());
        memberDTO.setNickname(memberEntity.getNickname());
        memberDTO.setProfilePicture(memberEntity.getProfilePicture());
        memberDTO.setCreatedAt(memberEntity.getCreatedAt());
        memberDTO.setUserStatus(memberEntity.getUserStatus());
        return memberDTO;
    }
}
