package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

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

    @Enumerated(EnumType.STRING)
//    @ElementCollection(fetch = FetchType.EAGER) // MemberEntity 조회시 조인쿼리를 이용해서 Authority도 함께 조회
    @ElementCollection(fetch = FetchType.LAZY) // (기본값) Member 우선 조회, authorities는 proxy처리해두고, 필요할때 조회
    // LAZY전략은 영속성컨텍스트 밖에서 proxy에 대한 조회를 시도하면, LazyInitializationException을 유발!
    @CollectionTable(
            name = "tbl_authority",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Authority> authorities;

    public void setDefaultAuthorities() {
        this.authorities = Set.of(Authority.ROLE_USER);
    }

    public void changeName(String name) {
        this.setName(name);
    }
}