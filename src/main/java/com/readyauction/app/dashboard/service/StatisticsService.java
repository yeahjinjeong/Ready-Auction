package com.readyauction.app.dashboard.service;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.dashboard.dto.TransactionStatisticsDto;
import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.UserStatus;
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
    private UserStatus userStatus;

    // 회원 통계 (성별, 나이대, 회원 상태)
    public List<MemberStatisticsDto> getMembersByStatus(UserStatus status) {
        return memberRepository.findByUserStatus(status).stream()
                .map(member -> new MemberStatisticsDto(
                        member.getGender(),
                        member.getBirth(),
                        member.getUserStatus()
                ))
                .collect(Collectors.toList());
    }


}

//    public int getCustomersCountForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
//        return memberRepository.countByCreatedAtBetween(
//                Timestamp.valueOf(startDate),
//                Timestamp.valueOf(endDate)
//        );
//    }
//
//    // 고객 수 통계 계산 메서드
//    public int getCustomersCountForToday() {
//        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
//        LocalDateTime todayEnd = todayStart.plusDays(1);
//        return getCustomersCountForPeriod(todayStart, todayEnd);
//    }
//
//    public int getCustomersCountForMonth() {
//        LocalDateTime monthStart = LocalDateTime.now().minusDays(30).toLocalDate().atStartOfDay();
//        LocalDateTime monthEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);
//        return getCustomersCountForPeriod(monthStart, monthEnd);
//    }
//
//    public int getCustomersCountForYear() {
//        LocalDateTime yearStart = LocalDateTime.now().minusDays(365).toLocalDate().atStartOfDay();
//        LocalDateTime yearEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);
//        return getCustomersCountForPeriod(yearStart, yearEnd);
//    }
//
//
//    // 오늘의 거래 완료 수 조회
//    public List<TransactionStatisticsDto> getTransactionsForToday() {
//        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
//        LocalDateTime todayEnd = todayStart.plusDays(1);
//
//        return productRepository.findCompletedTransactionsInTimeRange(todayStart, todayEnd, AuctionStatus.END);
//    }
//
//    // 주간 거래 완료 수 조회
//    public List<TransactionStatisticsDto> getTransactionsForWeek() {
//        LocalDateTime weekStart = LocalDateTime.now().minusDays(7).toLocalDate().atStartOfDay();
//        LocalDateTime weekEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);
//
//        return productRepository.findCompletedTransactionsInTimeRange(weekStart, weekEnd, AuctionStatus.END);
//    }
//
//    // 월간 거래 완료 수 조회
//    public List<TransactionStatisticsDto> getTransactionsForMonth() {
//        LocalDateTime monthStart = LocalDateTime.now().minusDays(30).toLocalDate().atStartOfDay();
//        LocalDateTime monthEnd = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);
//
//        return productRepository.findCompletedTransactionsInTimeRange(monthStart, monthEnd, AuctionStatus.END);
//    }
//
//    // 거래 완료 통계
//    public List<TransactionStatisticsDto> getCompletedTransactions() {
//        return productRepository.findCompletedTransactions(AuctionStatus.END);
//    }

