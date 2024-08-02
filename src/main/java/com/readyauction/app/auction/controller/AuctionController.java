package com.readyauction.app.auction.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
@Controller
@Slf4j
@RequestMapping("auction")
public class AuctionController {

    @GetMapping("/productUpload")// 상품 등록
    public void createAuction(Model model) {

    }

    @GetMapping("auction")// 상품 조회
    public void searchAuction(Model model) {

    }

    @GetMapping("/auction-details")// 경매 입찰 하는 상품 상세 페이지
    public void auctionDetails(Model model) {

    }

    @GetMapping("/report") // 신고 페이지
    public void report(Model model) {

    }

}
