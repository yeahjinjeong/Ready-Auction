package com.readyauction.app.dashboard.dto;


import com.readyauction.app.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberStatisticsDto {
    private Gender gender;
    private LocalDate birth;
}

