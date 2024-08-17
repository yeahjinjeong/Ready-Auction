package com.readyauction.app.cash.controller;

import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.entity.Cash;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.cash.service.CashService;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final MemberService memberService;
    private final CashService cashService;
    private final ProductService productService;
    private final AccountService accountService;

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
    @GetMapping("/success/{id}")
    public String getSuccess(@PathVariable("id") Long id, Model model) {
        try {
            System.out.println("실행중");
            ProductRepDto productDetail = productService.productDetail(id);
            if (productDetail != null) {
                model.addAttribute("productDetail", productDetail);
                return "cash/success"; // 여기 수정
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
    public String charge(Model model) {
        log.info("GET /cash-charge");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // 사용자 조회
        Long memberId = memberService.findMemberByEmail(currentUserName).getId();

        AccountDto accountDto = accountService.findAccountDtoByMemberId(memberId);
        log.debug("AccountDto: {}", accountDto);
        model.addAttribute("accountDto", accountDto);

        return "cash/cash-charge";
    }

    // 캐시 충전
    @PostMapping("/cash-charge")
    public String chargeCash(@ModelAttribute Cash cash, RedirectAttributes redirectAttributes) {
        log.info("POST /cash-charge");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // 사용자 조회
        Long memberId = memberService.findMemberByEmail(currentUserName).getId();

        try {
            cashService.chargeCash(memberId, cash);
            redirectAttributes.addFlashAttribute("message", "성공적으로 충전되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "충전 중 문제가 발생했습니다.");
        }

        return "redirect:/mypage";
    }

    // 캐시 환불
    @GetMapping("/cash-withdrawal")
    public String refund(Model model) {
        log.info("GET /cash-withdrawal");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // 사용자 조회
        Long memberId = memberService.findMemberByEmail(currentUserName).getId();

        AccountDto accountDto = accountService.findAccountDtoByMemberId(memberId);
        log.debug("AccountDto: {}", accountDto);
        model.addAttribute("accountDto", accountDto);

        return "cash/cash-withdrawal";
    }

    // 캐시 환불
    @PostMapping("/cash-withdrawal")
    public String refundCash(@ModelAttribute Cash cash, RedirectAttributes redirectAttributes, @RequestParam(value="withdrawal") Integer withdrawal) {
        log.info("POST /cash-withdrawal");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // 사용자 조회
        Long memberId = memberService.findMemberByEmail(currentUserName).getId();

        try {
            cashService.withdrawCash(memberId, cash, withdrawal);
            redirectAttributes.addFlashAttribute("message", "성공적으로 출금되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "출금 중 문제가 발생했습니다.");
            return "redirect:/cash-withdrawal";
        }

        return "redirect:/mypage";
    }
}
