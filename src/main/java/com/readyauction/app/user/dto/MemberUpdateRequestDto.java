package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {
    @NotBlank
    private Long id;
    @NotBlank
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

    public static MemberUpdateRequestDto toUpdateMemberDto(Member memberEntity) {
        MemberUpdateRequestDto dto = new MemberUpdateRequestDto();
        dto.setPassword(memberEntity.getPassword());
        dto.setName(memberEntity.getName());
        dto.setPhone(memberEntity.getPhone());
        dto.setAddress(memberEntity.getAddress());
        dto.setBirth(dto.getBirth());
        dto.setNickname(memberEntity.getNickname());
        dto.setProfilePicture(memberEntity.getProfilePicture());
        dto.setMannerScore(memberEntity.getMannerScore());
        dto.setCreatedAt(memberEntity.getCreatedAt());
//        memberDTO.setUpdatedAt(memberEntity.getUpdatedAt());
        dto.setUserStatus(memberEntity.getUserStatus());
        return dto;
    }
}
