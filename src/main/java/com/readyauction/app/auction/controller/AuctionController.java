package com.readyauction.app.auction.controller;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("auction")
@RequiredArgsConstructor
public class AuctionController {

    private final ProductService productService;

    @GetMapping("/productUpload") // 상품 등록
    public void createAuction(Model model) {
        // 상품 등록 로직
    }

    @GetMapping("/auction") // 상품 조회
    public String searchAuction(Model model) {
        List<ProductDto> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "auction/auction";
    }

    @GetMapping("/auction-details") // 경매 입찰 하는 상품 상세 페이지
    public void auctionDetails(Model model) {
        // 상세 페이지 로직
    }

    @GetMapping("/report") // 신고 페이지
    public void report(Model model) {
        // 신고 페이지 로직
    }

}
