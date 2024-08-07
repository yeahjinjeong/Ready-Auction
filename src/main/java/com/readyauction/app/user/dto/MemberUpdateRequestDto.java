package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Integer mannerScore;
    private Gender gender;
    private LocalDate birthday;
    private String nickname;
    private String profilePicture;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private UserStatus userStatus;
}
