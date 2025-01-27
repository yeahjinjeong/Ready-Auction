package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@DiscriminatorValue("adm") // Admin타입 구분하는 값을 adm로 지정 (기본값: Admin)
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
public class Admin extends User {
    // Getters and Setters
    @Builder
    public Admin(String email, String password, String name, String phone, UserStatus userStatus) {
        super(email, password, name, phone, userStatus, Set.of(Authority.ROLE_ADMIN));
    }
}