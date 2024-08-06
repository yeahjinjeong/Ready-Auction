package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("adm") // Admin타입 구분하는 값을 adm로 지정 (기본값: Admin)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Admin extends User {
    // Getters and Setters
}