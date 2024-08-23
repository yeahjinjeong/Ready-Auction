package com.readyauction.app.cash.service;

import com.readyauction.app.auction.dto.EmailMessage;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.service.EmailService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.dto.PaymentResDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Payment;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    final private PaymentRepository paymentRepository;
    final private AccountService accountService;
    final private MemberService memberService;
    final private ProductService productService;
    final private EmailService emailService;
    //입찰시 만들어지는 페이먼트

    @Transactional
    public Payment createBidPayment(Long userId, PaymentReqDto paymentReqDto) throws Exception {
        System.out.println("상품 입찰 선불금 지불 중!");
        try {
            System.out.println("거래 입금 내역");
            // 보낸이 조회
            Long senderId = userId;
            if (senderId == null) {
                throw new EntityNotFoundException("Sender not found for senderId: " + senderId);
            }

            // 보낸이 계좌 조회
            Account senderAccount = accountService.findByMemberId(senderId);
            if (senderAccount == null) {
                throw new EntityNotFoundException("Sender's account not found for user ID: " + senderId);
            }

            // 보낸이 계좌에서 출금
            accountService.withdrawal(senderId, paymentReqDto.getAmount());

            Product product = productService.findById(paymentReqDto.getProductId()).orElseThrow();
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
                    .category(paymentReqDto.getCategory())
                    .build();

            // Payment 저장
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment == null) {
                throw new EntityNotFoundException("Failed to save the payment");
            }

            return (savedPayment);

        } catch (EntityNotFoundException e) {
            // 특정 엔티티를 찾지 못했을 때의 예외 처리
            throw new Exception("Error during payment creation: " + e.getMessage(), e);
        }  catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new Exception("Database error during saving payment: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            // 출금할 때 잔액 부족 예외 처리
            throw new Exception("Unexpected error occurred during payment creation: " + e.getMessage(), e);
        }
    }

    public Boolean rollbackMoney(List<Payment> payments) {
        try {
            System.out.println("입찰 페이먼트 롤백 중 ");
            // 맴버아이디스를 다 조회해서 롤백으로 스테이터스를 바꾸고, 돈을 보낸이들에게 해당 금액을 돌려주는 로직.
            payments.forEach(payment -> {
                try {
                    System.out.println(payment.getId() + "payment 롤백중");
                    accountService.deposit(payment.getSenderAccount().getId(), payment.getPayAmount());
                    payment.setStatus(PaymentStatus.ROLLBACK_COMPLETED);
                    paymentRepository.save(payment);

                    EmailMessage emailMessage = EmailMessage.builder()
                            .to(memberService.findEmailById(payment.getMemberId()))
                            .subject("중고 스포츠 유니폼 판매 플랫폼 레디옥션입니다.")
                            .message("<html><head></head><body><div style=\"background-color: gray;\">"+productService.findById(payment.getProductId()).orElseThrow().getName() + " 경매에서 낙찰에 실패 했습니다. 입찰금은 환불처리돼 계좌에 입금 됐습니다! "+"<div></body></html>")
                            .build();
                    emailService.sendMail(emailMessage);
                } catch (Exception e) {
                    // 개별 입금 실패 예외 처리
                    System.err.println("Failed to deposit money for Payment ID: " + payment.getId() + ", Member ID: " + payment.getMemberId());
                    e.printStackTrace();
                    // 로그만 남기고 다음 결제로 진행
                }
            });

            return true;
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error occurred during money rollback: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during money rollback: " + e.getMessage(), e);
        }
    }


    public PaymentResDto createPayment(String email, PaymentReqDto paymentReqDto) {
        try {
            // 보낸이 조회
// 1        PaymentCategory.BID_COMPLETE이고,  PaymentStatus.PROCESSING이거나,PaymentStatus.COMPLETED인게 있으면 이미 결제가 진행했던거다.
            System.out.println(paymentReqDto.getProductId() +"프로덕트 아이디");
            Optional<List<Payment>> payments = paymentRepository.findByProductIdAndMemberIdAndCategory(
                    paymentReqDto.getProductId(),
                    memberService.findByEmail(email).getId(),
                    PaymentCategory.BID_COMPLETE);

            if(payments.isPresent() && !payments.get().isEmpty()){
                throw new EntityNotFoundException("Payment already exists");
            };
            System.out.println("결제 하기 페이먼트 생성");
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
                    .category(PaymentCategory.BID_COMPLETE)
                    .build();

            // Payment 저장
            Payment savedPayment = paymentRepository.save(payment);

            //비 낙찰자들 돈 롤백
            rollbackPayment(product.getId());
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

    public PaymentResDto completePayment(String email, PaymentReqDto paymentReqDto) {
        try {

            // 보낸이 조회
            Long senderId = memberService.findByEmail(email).getId();

            if (senderId == null) {
                throw new EntityNotFoundException("Sender not found for email: " + email);
            }
            // 구매완료가 이미 됐나 확인
            Payment payment = paymentRepository.findByMemberIdAndProductIdAndCategory(senderId,paymentReqDto.getProductId(), PaymentCategory.BID_COMPLETE).orElse(null);
            if(payment.getStatus() == PaymentStatus.COMPLETED) {
                throw new EntityNotFoundException("Payment is already completed");
            }
            // 받은이 계좌에 입금
            accountService.deposit(payment.getReceiverAccount().getId(), paymentReqDto.getAmount());

            // 낙찰로그에서 거래완료로 상태값 바꾸기
            Product product = productService.progressWinnerAccepted(paymentReqDto.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product not found for ID: " + paymentReqDto.getProductId());
            }

            // 위너 저장

            payment.setStatus(PaymentStatus.COMPLETED);
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

    public Boolean rollbackPayment(Long productId) {
        // 맴버아이디스를 다 조회해서 롤백으로 스테이터스를 바꾸고, 돈을 보낸이들에게 해당 금액을 돌려주는 로직.
//        paymentRepository.findByMemberIdAndProductId()
        paymentRepository.updateStatusToRollbackByProductIdAndCategory(productId,PaymentCategory.BID,PaymentStatus.PROCESSING);
        rollbackMoney(paymentRepository.findByProductIdAndStatus(productId, PaymentStatus.ROLLBACK).orElseThrow());
        return true;
    }

    private PaymentResDto convertToPaymentResDto(Payment payment) {
        return PaymentResDto.builder()
                .amount(payment.getPayAmount())
                .payTime(payment.getDate())
                .productId(payment.getProductId())
                .build();
    }

    /** 지영 - 경매 등록 내역 조회 시 필요 **/

    // 거래 완료 내역 (payment의 status가 COMPLETED인 경우)
    public List<Product> getCompletedProducts(Long memberId) {
        List<Long> productIds = findCompletedProductIdsByMemberId(memberId, PaymentStatus.COMPLETED);
        return productService.findByIdIn(productIds);
    }

    // 거래 완료 내역
    public List<Long> findCompletedProductIdsByMemberId(Long memberId, PaymentStatus paymentStatus) {
        return paymentRepository.findCompletedProductIdsByMemberId(memberId, paymentStatus);
    }
}
