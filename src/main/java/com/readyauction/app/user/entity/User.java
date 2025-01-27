package com.readyauction.app.user.entity;

import com.readyauction.app.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type") // 자식클래스 타입을 결정하는 컬럼명
@Getter
@NoArgsConstructor
public abstract class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "tbl_user_authorities")
    @Column(name = "authority")
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities;

    public User(
            String email,
            String password,
            String name,
            String phone,
            UserStatus userStatus,
            Set<Authority> authorities) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.userStatus = userStatus;
        this.authorities = authorities;
    }
    protected void changeName(String name) {
        this.name = name;
    }
    protected void changeUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}