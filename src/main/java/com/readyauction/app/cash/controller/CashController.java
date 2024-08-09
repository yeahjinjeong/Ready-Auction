package com.readyauction.app.cash.controller;

import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("cash")
@RequiredArgsConstructor
public class CashController {

    private final ProductService productService;

    @GetMapping("/payment/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        try {
            System.out.println("실행중");
            ProductRepDto productDetail = productService.productDetail(id);
            if (productDetail != null) {
                model.addAttribute("productDetail", productDetail);
                return "cash/payment"; // 여기 수정
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
