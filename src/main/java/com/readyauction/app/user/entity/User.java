package com.readyauction.app.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type") // 자식클래스 타입을 결정하는 컬럼명
@Data
@SuperBuilder
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

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false, unique = true)
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

    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "user", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "authority")
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities;

    public User(String address, String nickname, String profilePicture) {
        this.address = address;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }
}