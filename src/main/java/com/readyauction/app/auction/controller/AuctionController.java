package com.readyauction.app.auction.controller;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/auctionDetails")// 경매 입찰 하는 상품 상세 페이지
    public void auctionDetails() {

    }

    @GetMapping("/report") // 신고 페이지
    public void report(Model model) {
        // 신고 페이지 로직
    }

    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        try {
            System.out.println("실행중");
            ProductRepDto productDetail = productService.productDetail(id);
            if (productDetail != null) {
                model.addAttribute("productDetail", productDetail);
                return "auction/auctionDetails"; // 여기 수정
            } else {
                System.out.println("에러");
                return "error/404"; // 객체가 null일 경우 에러 페이지로 리다이렉션
            }
        } catch (IllegalStateException e) {
            log.error("Error fetching product details", e);
            return "error/404"; // 예외 발생 시 에러 페이지로 리다이렉션
        }
    }

}
