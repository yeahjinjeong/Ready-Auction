package com.readyauction.app.admin.dto;

import com.readyauction.app.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class UserManagementDto {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private Gender gender;
    private String address;
    private String phone;
    private LocalDate birth;
    private Timestamp statusTimestamp; // createdAt 또는 deletedAt
    private String status; // active 또는 deleted
    private Timestamp createdAt; // 가입일
    private Timestamp deletedAt; // 탈퇴일
    private Integer mannerScore;
}