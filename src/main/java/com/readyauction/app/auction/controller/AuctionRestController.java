package com.readyauction.app.auction.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("auction-api")
public class AuctionRestController {
    @PostMapping("")// 상품 등록
    public String createAuction() {
        // 상품 등록 구현
        return "등록 된 상품 정보 json 형식으로 반환";
    }
    @PostMapping("Update")
    public String updateAuction() {
        return "업데이트 된 상품정보 json 형식으로 반환";
    }

}
