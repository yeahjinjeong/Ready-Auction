package com.readyauction.app.auction.controller;


import com.readyauction.app.auction.dto.EmailMessage;
import com.readyauction.app.auction.dto.EmailPostDto;
import com.readyauction.app.auction.dto.EmailResponseDto;
import com.readyauction.app.auction.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    // 임시 비밀번호 발급
    @PostMapping("/password")
    public ResponseEntity sendPasswordMail(@RequestBody EmailPostDto emailPostDto) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostDto.getEmail())
                .subject("[SAVIEW] 임시 비밀번호 발급")
                .build();

        emailService.sendMail(emailMessage);


        return ResponseEntity.ok().build();
    }

    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/email")
    public ResponseEntity sendJoinMail(@RequestBody EmailPostDto emailPostDto) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostDto.getEmail())
                .subject("[SAVIEW] 이메일 인증을 위한 인증 코드 발송")
                .build();

        emailService.sendMail(emailMessage);

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setCode("code");

        return ResponseEntity.ok(emailResponseDto);
    }
    @PostMapping("/send-mail")
    public ResponseEntity sendMail() {
        EmailMessage emailMessage = EmailMessage.builder()
                .to("gim670079@gmail.com")
                .subject("테스트 메일 제목")
                .message("<html><head></head><body><div style=\"background-color: gray;\">테스트 메일 본문<div></body></html>")
                .build();
        emailService.sendMail(emailMessage);
        System.out.println("성공 이메일 보내기");
        return new ResponseEntity(HttpStatus.OK);
    }
}
