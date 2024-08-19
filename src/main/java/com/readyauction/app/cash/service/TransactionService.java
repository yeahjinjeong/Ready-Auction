package com.readyauction.app.cash.service;

import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.TransactionDto;
import com.readyauction.app.cash.entity.Cash;
import com.readyauction.app.cash.entity.Payment;
import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.CashRepository;
import com.readyauction.app.cash.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final CashRepository cashRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;

    public List<TransactionDto> getTransactionHistory(Long accountId) {
        // Cash 내역 조회
        List<Cash> cashList = cashRepository.findAllByAccountIdOrderByDateDesc(accountId);

        // Payment 내역 조회 및 필터링 - 결제 완료 Completed와 환불 완료 Rollback_Completed만 보여주기
        List<Payment> paymentList = paymentRepository.findAllByAccountIdOrderByDateDesc(accountId);
        List<Payment> filteredPayments = paymentList.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.ROLLBACK_COMPLETED)
                .collect(Collectors.toList());

        List<TransactionDto> transactions = new ArrayList<>();

        // Cash 내역 추가
        for (Cash cash : cashList) {
            // 캐시 상태
            String cashStatus = cash.getStatus() != null ? cash.getStatus().name() : "UNKNOWN";

            transactions.add(new TransactionDto(cash.getDate(), "CASH", cash.getAmount(), cashStatus, null));
        }

        // Payment 내역 추가
        for (Payment payment : filteredPayments) {
            // senderAccountId와 회원 accountId가 같으면 구매자, receiverAccountId와 회원 accountId가 같으면 판매자
            String paymentType = payment.getSenderAccount().getId().equals(accountId) ? "PAYMENT_SENT" : "PAYMENT_RECEIVED";

            // 결제 상태 (Completed 결제완료, Rollback_Completed 환불완료)
            String paymentStatus = payment.getStatus() != null ? payment.getStatus().name() : "UNKNOWN";
            
            Integer payAmount;
            
            // 회원 본인이 구매자일 경우
            if (paymentType.equals("PAYMENT_SENT")) {
                // 결제 완료인 경우
                if (paymentStatus.equals("COMPLETED")) {
                    payAmount = -payment.getPayAmount(); // 음수로 설정 (빨간색)
                // 환불 완료인 경우
                } else if (paymentStatus.equals("ROLLBACK_COMPLETED")) {
                    payAmount = payment.getPayAmount(); // 양수로 설정 (파란색)
                } else {
                    continue; // 다른 상태는 무시
                }
            // 회원 본인이 판매자일 경우
            } else { // PAYMENT_RECEIVED
                // 결제 완료인 경우
                if (paymentStatus.equals("COMPLETED")) {
                    payAmount = payment.getPayAmount(); // 양수로 설정 (파란색)
                } else if (paymentStatus.equals("ROLLBACK_COMPLETED")) {
                    payAmount = -payment.getPayAmount(); // 음수로 설정 (빨간색)
                } else {
                    continue; // 다른 상태는 무시
                }
            }

            // Product 이름 조회
            String productName = payment.getProductId() != null ? productService.findProductNameById(payment.getProductId()) : "N/A";

            transactions.add(new TransactionDto(payment.getDate(), paymentType, payAmount, paymentStatus, productName));
        }

        // 날짜 기준으로 내림차순 정렬
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        return transactions;
    }
}
