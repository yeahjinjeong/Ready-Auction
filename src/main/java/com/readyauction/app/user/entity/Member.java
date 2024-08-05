package com.readyauction.app.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("mem") // Member타입 구분하는 값을 mem로 지정 (기본값: Member)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Member extends User {
    private Long memberId;
    private Integer mannerScore; // 매너지수
    // Getters and Setters
}