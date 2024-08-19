package com.readyauction.app.dashboard.dto;


import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberStatisticsDto {
    private Gender gender;
    private LocalDate birth;
    private UserStatus userStatus; // 회원 상태 추가
}

