package com.readyauction.app.dashboard.controller;

import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.dashboard.dto.TransactionStatisticsDto;
import com.readyauction.app.dashboard.service.StatisticsService;
import com.readyauction.app.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    @GetMapping("/members")
    public List<MemberStatisticsDto> getMembers(@RequestParam("status") String status) {
        UserStatus userStatus = UserStatus.valueOf(status); // 문자열을 그대로 UserStatus enum으로 변환
        return statisticsService.getMembersByStatus(userStatus);
    }

}

//    // 특정 기간 동안의 회원 통계 조회
//    @GetMapping("/members/period")
//    public List<MemberStatisticsDto> getMemberStatisticsByPeriod(
//            @RequestParam("startDate") LocalDate startDate,
//            @RequestParam("endDate") LocalDate endDate) {
//        return statisticsService.getMemberStatisticsByPeriod(startDate, endDate);
//    }
//
//    // 오늘의 고객 수
//    @GetMapping("/customers/today")
//    public int getCustomersForToday() {
//        return statisticsService.getCustomersCountForToday();
//    }
//
//    // 월간 고객 수
//    @GetMapping("/customers/month")
//    public int getCustomersForMonth() {
//        return statisticsService.getCustomersCountForMonth();
//    }
//
//    // 연간 고객 수
//    @GetMapping("/customers/year")
//    public int getCustomersForYear() {
//        return statisticsService.getCustomersCountForYear();
//    }
//
//    @GetMapping("/transactions/completed")
//    public List<TransactionStatisticsDto> getCompletedTransactions() {
//        return statisticsService.getCompletedTransactions();
//    }
//
//    @GetMapping("/transactions/today")
//    public List<TransactionStatisticsDto> getTodayTransactions() {
//        return statisticsService.getTransactionsForToday();
//    }
//
//    @GetMapping("/transactions/week")
//    public List<TransactionStatisticsDto> getWeekTransactions() {
//        return statisticsService.getTransactionsForWeek();
//    }
//
//    @GetMapping("/transactions/month")
//    public List<TransactionStatisticsDto> getMonthTransactions() {
//        return statisticsService.getTransactionsForMonth();
//    }
//
//

