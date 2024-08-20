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
    private Timestamp statusTimestamp; // createdAt or deletedAt
}
