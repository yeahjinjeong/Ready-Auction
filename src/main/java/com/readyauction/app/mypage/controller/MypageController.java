package com.readyauction.app.mypage.controller;

import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.dto.TransactionDto;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.cash.service.TransactionService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.service.InquiryService;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.ProfileDto;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
@Slf4j
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BidService bidService;
    private final ProductService productService;
    private final InquiryService inquiryService;

    // 마이페이지
    @GetMapping("")
    public String getMyPage(Model model) {
        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 로그인한 이메일
        Long memberId = memberService.findMemberDtoByEmail(currentUserName).getId();

        try {
            // MemberDto 가져오기
            MemberDto memberDto = memberService.findMemberDtoByEmail(currentUserName);
            log.debug("memberDto: {}", memberDto);
            model.addAttribute("memberDto", memberDto);

            // ProfileDto 가져오기
            ProfileDto profileDto = memberService.findProfileDtoById(memberId);
            model.addAttribute("profileDto", profileDto);

            // 경매 참여 내역 조회
            // 입찰 중 내역
            List<Bid> biddingBids = bidService.getBiddingBids(memberId);
            model.addAttribute("biddingBids", biddingBids);

            // 낙찰 내역
            List<Product> winningBids = productService.getWinningBids(memberId);

            // 결제 전 결제하기 버튼
            Map<Long, Boolean> isWinnerConfirmedMap = new HashMap<>();
            // 결제 완료 - 결제하기 버튼 사라짐
            Map<Long, Boolean> isPaymentCompleteMap = new HashMap<>();

            for (Product product : winningBids) {
                Long productId = product.getId();
                isWinnerConfirmedMap.put(productId, productService.isWinnerConfirmed(productId, memberId));
                isPaymentCompleteMap.put(productId, productService.isPaymentComplete(productId, memberId));
            }

            model.addAttribute("winningBids", winningBids);
            model.addAttribute("isWinnerConfirmedMap", isWinnerConfirmedMap);
            model.addAttribute("isPaymentCompleteMap", isPaymentCompleteMap);

            // 패찰 내역
            List<Bid> losingBids = bidService.getLosingBids(memberId);
            model.addAttribute("losingBids", losingBids);

            // 경매 등록 내역 조회
            // 판매 중 내역
            List<Product> activeProducts = productService.getActiveProducts(memberId);
            model.addAttribute("activeProducts", activeProducts);

            // 거래 완료 내역 (결제 완료, 구매 확정)
            List<Product> completedProducts = productService.getCompletedProducts(memberId);
            model.addAttribute("completedProducts", completedProducts);

            // 유찰 내역
            List<Product> failedProducts = productService.getFailedProducts(memberId);
            model.addAttribute("failedProducts", failedProducts);

            // 1:1 문의 내역
            List<Inquiry> inquiries = inquiryService.getInquiriesByUserId(memberId);
            model.addAttribute("inquiries", inquiries);

        } catch (UserNotFoundException e) {
            log.error("Member not found: {}", e.getMessage());
            return "error/404";
        }

        return "mypage/mypage";
    }
    
    // 프로필 수정
    @GetMapping("/profile/update")
    public String updateProfile(Model model) {
        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        Long memberId = memberService.findMemberDtoByEmail(currentUserName).getId();

        ProfileDto profileDto = memberService.findProfileDtoById(memberId);
        log.debug("profileDto: {}", profileDto);
        model.addAttribute("profileDto", profileDto);

        return "mypage/profile-update";
    }

    // 프로필 수정
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam(value="isRemovedImage") boolean isRemovedImage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            memberService.updateProfile(currentUserName, nickname, image, isRemovedImage);
        } catch (IOException e) {
            log.error("Error updating profile", e);
            return "error/404";
        }

        return "redirect:/mypage";
    }

//    @GetMapping("/data")
//    public @ResponseBody PageResponse getPageData(@RequestParam("page") int pageNum) {
//        Pageable pageable = PageRequest.of(pageNum - 1, 10); // 페이지 번호는 0부터 시작하므로 -1합니다.
//        Page<MyEntity> page = myService.getEntities(pageable);
//
//        PageResponse response = new PageResponse();
//        response.setPageNum(pageNum);
//        response.setContent(page.getContent());
//        return response;
//    }

    // 회원정보 수정
    @GetMapping("/userInfo/edit")
    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
