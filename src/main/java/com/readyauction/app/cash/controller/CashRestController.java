package com.readyauction.app.cash.controller;

import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.dto.PaymentResDto;
import com.readyauction.app.cash.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("cash-api")
@RequiredArgsConstructor
public class CashRestController {
    final PaymentService paymentService;
    @PostMapping("/payment")
    public ResponseEntity<PaymentResDto> createAuction(@RequestBody PaymentReqDto paymentReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        paymentReqDto.setCategory(PaymentCategory.BID_COMPLETE);
        PaymentResDto createPayment = paymentService.createPayment(email, paymentReqDto);


        if (createPayment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createPayment);
    }


    @PostMapping("/success")
    public ResponseEntity<PaymentResDto> createSuccess(@RequestBody PaymentReqDto paymentReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        PaymentResDto createPayment = paymentService. completePayment(email, paymentReqDto);
        if (createPayment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createPayment);
    }
}
