package com.readyauction.app.cash.controller;

import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.cash.service.ChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private ProductService productService;

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


    /** 캐시 **/

    // 캐시 충전
    @GetMapping("/cash-charge")
    public String charge() {
        return "cash/cash-charge";
    }

    // 캐시 충전
    @PostMapping("/charge")
    public String chargeCash(@RequestParam Long memberId, @RequestParam Integer chargeAmount) {
        // Find account by memberId
//        Account account = accountService.findById(memberId);
//
//        // Update account balance
//        accountService.updateCash(account.getId(), chargeAmount);
//
//        // Record charge
//        Charge charge = new Charge();
//        charge.setMemberId(memberId);
//        charge.setChargeAmount(chargeAmount);
//        charge.setDate(LocalDateTime.now());
//        charge.setChargeStatus("SUCCESS");
//        chargeService.saveCharge(charge);

        return "redirect:/mypage";
    }

    // 캐시 환불
    @GetMapping("/cash-refund")
    public String refund() {
        return "cash/cash-refund";
    }

    // 캐시 환불
    @PostMapping("/refund")
    public String refundCash(@RequestParam Long memberId, @RequestParam Integer refundAmount) {


        return "redirect:/mypage";
    }
}
