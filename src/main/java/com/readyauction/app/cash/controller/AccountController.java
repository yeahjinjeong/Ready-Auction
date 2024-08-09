package com.readyauction.app.cash.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    // 캐쉬 충전
    @GetMapping("/cash-charge")
    public String charge() {
        return "mypage/cash-charge";
    }
}
