package com.readyauction.app.dashboard.service;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.dashboard.dto.TransactionStatisticsDto;
import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.auction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 회원 통계 (성별, 나이대)
    public List<MemberStatisticsDto> getMemberStatistics() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberStatisticsDto(
                        member.getGender(),
                        member.getBirth() // 생년월일 반환
                ))
                .collect(Collectors.toList());
    }

    // 오늘의 거래 완료 수 조회
    public List<TransactionStatisticsDto> getTransactionsForToday() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        return productRepository.findCompletedTransactionsInTimeRange(todayStart, todayEnd, AuctionStatus.END);
    }

    // 주간 거래 완료 수 조회
    public List<TransactionStatisticsDto> getTransactionsForWeek() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7).toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);

        return productRepository.findCompletedTransactionsInTimeRange(weekStart, weekEnd, AuctionStatus.END);
    }

    // 월간 거래 완료 수 조회
    public List<TransactionStatisticsDto> getTransactionsForMonth() {
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30).toLocalDate().atStartOfDay();
        LocalDateTime monthEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);

        return productRepository.findCompletedTransactionsInTimeRange(monthStart, monthEnd, AuctionStatus.END);
    }

    // 거래 완료 통계
    public List<TransactionStatisticsDto> getCompletedTransactions() {
        return productRepository.findCompletedTransactions(AuctionStatus.END);
    }
}
