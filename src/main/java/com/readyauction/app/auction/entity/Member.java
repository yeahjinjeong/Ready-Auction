package com.readyauction.app.auction.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("mem") // Developer타입 구분하는 값을 dev로 지정 (기본값: Developer)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Member extends User {
    private String nickname;
    private String picture;
    private String address;
    private Integer mannersScore;
    private Integer cashPoint;
    // Getters and Setters
}