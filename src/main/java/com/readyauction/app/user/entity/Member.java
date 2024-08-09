package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
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
    // Getters and Setters

//    @Enumerated(EnumType.STRING)
////    @ElementCollection(fetch = FetchType.EAGER) // MemberEntity 조회시 조인쿼리를 이용해서 Authority도 함께 조회
//    @ElementCollection(fetch = FetchType.LAZY) // (기본값) Member 우선 조회, authorities는 proxy처리해두고, 필요할때 조회
//    // LAZY전략은 영속성컨텍스트 밖에서 proxy에 대한 조회를 시도하면, LazyInitializationException을 유발!
//    @CollectionTable(
//            name = "tbl_authority",
//            joinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<Authority> authorities;

    public void setDefaultAuthorities() {
        this.setAuthorities(Set.of(Authority.ROLE_USER));
    }

    public void changeName(String name) {
        this.setName(name);
    }

    public static Member toUpdateMember(MemberUpdateRequestDto dto) {
        Member Member = new Member();
        Member.setId(dto.getId());
        Member.setEmail(dto.getEmail());
        Member.setPassword(dto.getPassword());
        Member.setAddress(dto.getAddress());
        Member.setBirth(dto.getBirth());
        Member.setCreatedAt(dto.getCreatedAt());
        Member.setDeletedAt(dto.getDeletedAt());
        Member.setGender(dto.getGender());
        Member.setName(dto.getName());
        Member.setNickname(dto.getNickname());
        Member.setPhone(dto.getPhone());
        Member.setProfilePicture(dto.getProfilePicture());
        Member.setUpdatedAt(dto.getUpdatedAt());
        Member.setUserStatus(dto.getUserStatus());
        Member.setMannerScore(dto.getMannerScore());
        return Member;
    }
}