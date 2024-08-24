package com.readyauction.app.admin.dashboard.service;

import com.readyauction.app.admin.dashboard.dto.MemberStatisticsDto;
import com.readyauction.app.cash.entity.Payment;
import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    // 기간별 거래 체결 금액과 거래량 계산 로직
    public Map<String, Object> getTransactionStatistics(String period) {
        LocalDateTime startTime = getStartTimeForPeriod(period);
        LocalDateTime endTime = LocalDateTime.now();

        List<LocalDate> allDates = startTime.toLocalDate().datesUntil(endTime.toLocalDate().plusDays(1))
                .collect(Collectors.toList());

        List<Payment> payments = paymentRepository.findConfirmedPaymentsInTimeRange(startTime, endTime, PaymentStatus.COMPLETED);

        Map<LocalDate, List<Payment>> groupedByDate = payments.stream()
                .collect(Collectors.groupingBy(payment -> payment.getDate().toLocalDateTime().toLocalDate()));

        List<String> dates = new ArrayList<>();
        List<Long> transactionAmounts = new ArrayList<>();
        List<Long> transactionCounts = new ArrayList<>();

        for (LocalDate date : allDates) {
            List<Payment> paymentsForDate = groupedByDate.getOrDefault(date, Collections.emptyList());
            dates.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            transactionAmounts.add(paymentsForDate.stream().mapToLong(Payment::getPayAmount).sum());
            transactionCounts.add((long) paymentsForDate.size());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("periods", dates);
        response.put("transactionAmounts", transactionAmounts);
        response.put("transactionCounts", transactionCounts);

        return response;
    }

    private LocalDateTime getStartTimeForPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "today":
                return now.toLocalDate().atStartOfDay();
            case "week":
                return now.minusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "month":
                return now.minusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "year":
                return now.minusYears(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }
}



