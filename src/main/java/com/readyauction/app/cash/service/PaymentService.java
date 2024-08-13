package com.readyauction.app.cash.service;

import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.dto.PaymentResDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Payment;
import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.AccountRepository;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    final private PaymentRepository paymentRepository;
    final private AccountService accountService;
    final private MemberService memberService;
    final private ProductService productService;
    public PaymentResDto createPayment(String email, PaymentReqDto paymentReqDto) {
        try {
            // 보낸이 조회
            Long senderId = memberService.findByEmail(email).getId();
            if (senderId == null) {
                throw new EntityNotFoundException("Sender not found for email: " + email);
            }

            // 보낸이 계좌 조회
            Account senderAccount = accountService.findByMemberId(senderId);
            if (senderAccount == null) {
                throw new EntityNotFoundException("Sender's account not found for user ID: " + senderId);
            }

            // 보낸이 계좌에서 출금
            accountService.withdrawal(senderId, paymentReqDto.getAmount());

            // 낙찰로그에서 거래중으로 상태값 바꾸기
            Product product = productService.progressWinnerProcess(paymentReqDto.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product not found for ID: " + paymentReqDto.getProductId());
            }

            // 받는이 조회
            Long receiverId = product.getMemberId();
            if (receiverId == null) {
                throw new EntityNotFoundException("Receiver not found for product ID: " + product.getId());
            }

            // 받는이 계좌 조회
            Account receiverAccount = accountService.findByMemberId(receiverId);
            if (receiverAccount == null) {
                throw new EntityNotFoundException("Receiver's account not found for user ID: " + receiverId);
            }

            // 위너 저장
            Payment payment = Payment.builder()
                    .date(paymentReqDto.getPayTime())
                    .payAmount(paymentReqDto.getAmount())
                    .productId(product.getId())
                    .senderAccount(senderAccount)
                    .receiverAccount(receiverAccount)
                    .memberId(senderId)
                    .status(PaymentStatus.PROCESSING)
                    .build();

            // Payment 저장
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment == null) {
                throw new RuntimeException("Failed to save the payment");
            }

            return convertToPaymentResDto(savedPayment);

        } catch (EntityNotFoundException e) {
            // 특정 엔티티를 찾지 못했을 때의 예외 처리
            throw new RuntimeException("Error during payment creation: " + e.getMessage(), e);
        }  catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during saving payment: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            // 출금할 때 잔액 부족 예외 처리
            throw new RuntimeException("Unexpected error occurred during payment creation: " + e.getMessage(), e);
        }
    }

//
//    public PaymentResDto updatePayment(String email, PaymentReqDto paymentReqDto) {
//        try {
//            // 보낸이 조회
//            Long senderId = memberService.findByEmail(email).getId();
//            if (senderId == null) {
//                throw new EntityNotFoundException("Sender not found for email: " + email);
//            }
//
//            // 보낸이 계좌에서 출금
//            accountService.deposit(senderId, paymentReqDto.getAmount());
//
//            // 낙찰로그에서 거래중으로 상태값 바꾸기
//            Product product = productService.progressWinnerPending(paymentReqDto.getProductId());
//            if (product == null) {
//                throw new EntityNotFoundException("Product not found for ID: " + paymentReqDto.getProductId());
//            }
//
//            //페이먼트 서치
//
//            // 위너 저장
//
//
//            // Payment 저장
//            Payment savedPayment = paymentRepository.save(payment);
//            if (savedPayment == null) {
//                throw new RuntimeException("Failed to save the payment");
//            }
//
//            return convertToPaymentResDto(savedPayment);
//
//        } catch (EntityNotFoundException e) {
//            // 특정 엔티티를 찾지 못했을 때의 예외 처리
//            throw new RuntimeException("Error during payment creation: " + e.getMessage(), e);
//        }  catch (DataAccessException e) {
//            // 데이터베이스 관련 예외 처리
//            throw new RuntimeException("Database error during saving payment: " + e.getMessage(), e);
//        } catch (Exception e) {
//            // 기타 예외 처리
//            // 출금할 때 잔액 부족 예외 처리
//            throw new RuntimeException("Unexpected error occurred during payment creation: " + e.getMessage(), e);
//        }
//    }
//


    private PaymentResDto convertToPaymentResDto(Payment payment) {
        return PaymentResDto.builder()
                .amount(payment.getPayAmount())
                .payTime(payment.getDate())
                .productId(payment.getProductId())
                .build();
    }

}
