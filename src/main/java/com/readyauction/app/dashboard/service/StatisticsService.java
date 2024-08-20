package com.readyauction.app.dashboard.service;

import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private UserStatus status;


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

    //   기간 별 거래 체결 금액 구현 시작
    public long getTransactionAmountForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfDay, endOfDay, PaymentStatus.COMPLETED)
                .stream()
                .mapToLong(payment -> payment.getPayAmount())
                .sum();
    }

    public long getTransactionAmountForWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1); // 1주일 이내
        LocalDateTime endOfWeek = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfWeek, endOfWeek, PaymentStatus.COMPLETED)
                .stream()
                .mapToLong(payment -> payment.getPayAmount())
                .sum();
    }

    public long getTransactionAmountForMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1); // 1달 이내
        LocalDateTime endOfMonth = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfMonth, endOfMonth, PaymentStatus.COMPLETED)
                .stream()
                .mapToLong(payment -> payment.getPayAmount())
                .sum();
    }

    public long getTransactionAmountForYear() {
        LocalDateTime startOfYear = LocalDateTime.now().minusYears(1); // 1년 이내
        LocalDateTime endOfYear = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfYear, endOfYear, PaymentStatus.COMPLETED)
                .stream()
                .mapToLong(payment -> payment.getPayAmount())
                .sum();
    }
    // 기간 별 거래 체결 금액 구현 시작

    // 기간별 거래량 시작
    public long getTransactionCountForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfDay, endOfDay, PaymentStatus.COMPLETED).size();
    }

    public long getTransactionCountForWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1); // 1주일 이내
        LocalDateTime endOfWeek = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfWeek, endOfWeek, PaymentStatus.COMPLETED).size();
    }

    public long getTransactionCountForMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1); // 1달 이내
        LocalDateTime endOfMonth = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfMonth, endOfMonth, PaymentStatus.COMPLETED).size();
    }

    public long getTransactionCountForYear() {
        LocalDateTime startOfYear = LocalDateTime.now().minusYears(1); // 1년 이내
        LocalDateTime endOfYear = LocalDateTime.now();
        return paymentRepository.findConfirmedPaymentsInTimeRange(startOfYear, endOfYear, PaymentStatus.COMPLETED).size();
    }
    //   기간 별 거래량 구현 끝

}



