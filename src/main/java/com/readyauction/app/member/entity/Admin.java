package com.readyauction.app.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("adm") // Manager타입 구분하는 값을 manager로 지정 (기본값: Manager)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Admin extends User {
    private String nickname;
    private String picture;
    // Getters and Setters
}