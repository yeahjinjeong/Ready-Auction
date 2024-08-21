package com.readyauction.app.cash.service;

import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.TransactionDto;
import com.readyauction.app.cash.entity.*;
import com.readyauction.app.cash.repository.CashRepository;
import com.readyauction.app.cash.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final CashRepository cashRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;

    public List<TransactionDto> getTransactionHistory(Long memberId, Long accountId) {
        List<TransactionDto> transactions = new ArrayList<>();

        // Cash 내역 추가
        List<Cash> cashList = cashRepository.findAllByAccountId(accountId);
        for (Cash cash : cashList) {
            String cashStatus = cash.getStatus() != null ? cash.getStatus().name() : "UNKNOWN";
            transactions.add(new TransactionDto(cash.getDate(), "캐시", cash.getAmount(), "CHARGE".equals(cashStatus) ? "충전" : "출금", null));
        }

        // Payment 관련 내역 - 판매자 판매 완료 (입금) 제외
        List<Payment> paymentsByMemberId = paymentRepository.findAllByMemberId(memberId);
        String productName;

        for (Payment payment : paymentsByMemberId) {
            productName = productService.findProductNameById(payment.getProductId());

            // 선입금 환불 (입금) 및 출금 내역 보존
            if (payment.getStatus() == PaymentStatus.ROLLBACK_COMPLETED) {
                transactions.add(new TransactionDto(payment.getDate(), "선입금", payment.getPayAmount(), "환불", productName));
                transactions.add(new TransactionDto(payment.getDate(), "선입금", -payment.getPayAmount(), "지불", productName));
            } else if (payment.getStatus() == PaymentStatus.PROCESSING) {
                // 선입금 (출금)
                transactions.add(new TransactionDto(payment.getDate(), "선입금", -payment.getPayAmount(), "지불", productName));
            } else if (payment.getCategory() == PaymentCategory.BID_COMPLETE) {
                // 구매 (출금)
                transactions.add(new TransactionDto(payment.getDate(), "구매", -payment.getPayAmount(), "완료", productName));
            }
        }

        // Payment - 판매자 판매 완료 (입금)
        List<Payment> paymentsByAccountId = paymentRepository.findAllByAccountId(accountId);
        for (Payment payment : paymentsByAccountId) {
            productName = productService.findProductNameById(payment.getProductId());

            if (payment.getReceiverAccount().getMemberId().equals(memberId) && payment.getStatus() == PaymentStatus.COMPLETED) {
                transactions.add(new TransactionDto(payment.getDate(), "판매", payment.getPayAmount(), "완료", productName));
            }
        }

        // 날짜 기준으로 내림차순 정렬
        return transactions.stream()
                .sorted(Comparator.comparing(TransactionDto::getDate).reversed())
                .collect(Collectors.toList());
    }
}
