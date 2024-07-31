package com.readyauction.app.auction.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("auction-searching-api")
public class AuctionSearchRestController {
    @GetMapping("searching")// 상품 조회
    public String searchAuction() {
        return "검색 된 상품 정보 리스트로 반환";
    }

}
