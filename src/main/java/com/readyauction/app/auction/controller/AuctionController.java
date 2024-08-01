package com.readyauction.app.auction.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("auction")
public class AuctionController {

    @GetMapping("")// 상품 등록
    public String createAuction() {
        return "contact";
    }

    @GetMapping("searching")// 상품 조회
    public String searchAuction() {
        return "auction";
    }

    @GetMapping("/details")// 경매 입찰 하는 상품 상세 페이지
    public String auctionDetails() {
        return "auction-details";
    }

    @GetMapping("/report") // 신고 페이지
    public String report() {
        return "faq";
    }

}
