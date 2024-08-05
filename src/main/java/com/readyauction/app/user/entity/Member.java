package com.readyauction.app.user.entity;

import com.readyauction.app.user.dto.MemberDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("mem") // Member타입 구분하는 값을 mem로 지정 (기본값: Member)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Member extends User {
    private Long memberId;
    private Integer mannerScore; // 매너지수
    // Getters and Setters

    public static Member toMember(MemberDTO memberDTO) {
        Member Member = new Member();
        Member.setId(memberDTO.getId());
        Member.setEmail(memberDTO.getEmail());
        Member.setPassword(memberDTO.getPassword());
        Member.setName(memberDTO.getName());
        Member.setPhone(memberDTO.getPhone());
        Member.setAddress(memberDTO.getAddress());
        Member.setBirth(memberDTO.getBirthday());
        Member.setNickname(memberDTO.getNickname());
        Member.setProfilePicture(memberDTO.getProfilePicture());
        Member.setCreatedAt(memberDTO.getCreatedAt());
        Member.setUserStatus(memberDTO.getUserStatus());
        return Member;
    }

    public static Member toUpdateMember(MemberDTO memberDTO) {
        Member Member = new Member();
        Member.setId(memberDTO.getId());
        Member.setEmail(memberDTO.getEmail());
        Member.setPassword(memberDTO.getPassword());
        Member.setName(memberDTO.getName());
        return Member;
    }
}