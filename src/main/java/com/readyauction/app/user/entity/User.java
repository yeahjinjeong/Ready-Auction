package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type") // 자식클래스 타입을 결정하는 컬럼명
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // E-mail

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDate birth;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profilePicture;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private Timestamp updatedAt;

    @Column
    private Timestamp deletedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    // Getters and Setters
}