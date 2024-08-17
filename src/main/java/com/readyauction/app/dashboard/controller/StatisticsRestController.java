package com.readyauction.app.dashboard.controller;

import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.dashboard.dto.TransactionStatisticsDto;
import com.readyauction.app.dashboard.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    @GetMapping("/members")
    public List<MemberStatisticsDto> getMemberStatistics() {
        return statisticsService.getMemberStatistics();
    }

    @GetMapping("/transactions/completed")
    public List<TransactionStatisticsDto> getCompletedTransactions() {
        return statisticsService.getCompletedTransactions();
    }

    @GetMapping("/transactions/today")
    public List<TransactionStatisticsDto> getTodayTransactions() {
        return statisticsService.getTransactionsForToday();
    }

    @GetMapping("/transactions/week")
    public List<TransactionStatisticsDto> getWeekTransactions() {
        return statisticsService.getTransactionsForWeek();
    }

    @GetMapping("/transactions/month")
    public List<TransactionStatisticsDto> getMonthTransactions() {
        return statisticsService.getTransactionsForMonth();
    }
}
