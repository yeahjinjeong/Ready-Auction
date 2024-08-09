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
import com.readyauction.app.cash.repository.AccountRepository;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        //결제
        Long senderId = memberService.findByEmail(email).getId();
        Account senderAccount = accountService.findByMemberId(senderId);
        Product product = productService.findById(paymentReqDto.getProductId()).orElseThrow();
        Long receiverId = product.getMemberId();
        Account receiverAccount = accountService.findByMemberId(receiverId);
        Payment payment = Payment.builder()
                .date(paymentReqDto.getPayTime())
                .payAmount(paymentReqDto.getAmount())
                .productId(product.getId())
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .memberId(senderId)
                .build();

        return convertToPaymentResDto(paymentRepository.save(payment));

    }
    private PaymentResDto convertToPaymentResDto(Payment payment) {
        return PaymentResDto.builder()
                .amount(payment.getPayAmount())
                .payTime(payment.getDate())
                .productId(payment.getProductId())
                .build();
    }

}
