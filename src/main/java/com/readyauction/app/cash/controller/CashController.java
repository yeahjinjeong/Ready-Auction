package com.readyauction.app.cash.controller;

import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.dto.TransactionDto;
import com.readyauction.app.cash.entity.Cash;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.cash.service.CashService;
import com.readyauction.app.cash.service.TransactionService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.ProfileDto;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cash")
public class CashController {

    private final MemberService memberService;
    private final CashService cashService;
    private final ProductService productService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("")
    public String getCash(Model model) {
        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 로그인한 이메일
        Long memberId = memberService.findMemberDtoByEmail(currentUserName).getId();

        try {
            // AccountDto 가져오기
            AccountDto accountDto = accountService.findAccountDtoByMemberId(memberId);
            model.addAttribute("accountDto", accountDto);

            // 캐시와 결제 내역 조회
            List<TransactionDto> transactionHistory = transactionService.getTransactionHistory(memberId, accountDto.getId());
            model.addAttribute("transactionHistory", transactionHistory);

        } catch (UserNotFoundException e) {
            log.error("Member not found: {}", e.getMessage());
            return "error/404";
        }

        return "cash/cash";
    }

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
    @GetMapping("/charge")
    public String charge(Model model) {
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
    @PostMapping("/charge")
    public String chargeCash(@ModelAttribute Cash cash, RedirectAttributes redirectAttributes) {
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

        return "redirect:/cash";
    }

    // 캐시 환불
    @GetMapping("/withdrawal")
    public String refund(Model model) {
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
    @PostMapping("/withdrawal")
    public String refundCash(@ModelAttribute Cash cash, RedirectAttributes redirectAttributes, @RequestParam(value="withdrawal") Integer withdrawal) {
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
            return "redirect:/cash/cash-withdrawal";
        }

        return "redirect:/cash";
    }
}
