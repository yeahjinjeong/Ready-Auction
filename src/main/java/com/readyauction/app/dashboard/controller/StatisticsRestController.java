package com.readyauction.app.dashboard.controller;

import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.dashboard.service.StatisticsService;
import com.readyauction.app.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //   기간 별 거래 체결 금액 구현 시작
    @GetMapping("/transactions/amount/today")
    public Map<String, Object> getTodayTransactionAmount() {
        long transactionAmount = statisticsService.getTransactionAmountForToday();
        return createTransactionAmountResponse(transactionAmount);
    }

    @GetMapping("/transactions/amount/week")
    public Map<String, Object> getWeeklyTransactionAmount() {
        long transactionAmount = statisticsService.getTransactionAmountForWeek();
        return createTransactionAmountResponse(transactionAmount);
    }

    @GetMapping("/transactions/amount/month")
    public Map<String, Object> getMonthlyTransactionAmount() {
        long transactionAmount = statisticsService.getTransactionAmountForMonth();
        return createTransactionAmountResponse(transactionAmount);
    }

    @GetMapping("/transactions/amount/year")
    public Map<String, Object> getYearlyTransactionAmount() {
        long transactionAmount = statisticsService.getTransactionAmountForYear();
        return createTransactionAmountResponse(transactionAmount);
    }

    private Map<String, Object> createTransactionAmountResponse(long amount) {
        Map<String, Object> response = new HashMap<>();
        response.put("transactionAmount", amount);
        return response;
    }
    //     기간 별 낙찰금 구현 끝

    //   기간 별 거래량 구현 시작
    @GetMapping("/transactions/count/today")
    public Map<String, Object> getTodayTransactionCount() {
        long transactionCount = statisticsService.getTransactionCountForToday();
        return createTransactionCountResponse(transactionCount);
    }

    @GetMapping("/transactions/count/week")
    public Map<String, Object> getWeeklyTransactionCount() {
        long transactionCount = statisticsService.getTransactionCountForWeek();
        return createTransactionCountResponse(transactionCount);
    }

    @GetMapping("/transactions/count/month")
    public Map<String, Object> getMonthlyTransactionCount() {
        long transactionCount = statisticsService.getTransactionCountForMonth();
        return createTransactionCountResponse(transactionCount);
    }

    @GetMapping("/transactions/count/year")
    public Map<String, Object> getYearlyTransactionCount() {
        long transactionCount = statisticsService.getTransactionCountForYear();
        return createTransactionCountResponse(transactionCount);
    }

    private Map<String, Object> createTransactionCountResponse(long count) {
        Map<String, Object> response = new HashMap<>();
        response.put("transactionCount", count);
        return response;
    }

    //   기간 별 구매확정 거래량/금액 구현
    @GetMapping("/transactions/statistics")
    public Map<String, Object> getTransactionStatistics(@RequestParam("period") String period) {
        return statisticsService.getTransactionStatistics(period);
    }

}
