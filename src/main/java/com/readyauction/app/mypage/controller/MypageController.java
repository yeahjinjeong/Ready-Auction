package com.readyauction.app.mypage.controller;

import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.BidStatus;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.dto.TransactionDto;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.cash.service.TransactionService;
import com.readyauction.app.common.handler.UserNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    // 마이페이지
    @GetMapping("")
    public String getMyPage(Model model) {
        log.info("GET /mypage");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 로그인한 이메일
        System.out.println("currentUserName : " + currentUserName);

        try {
            // MemberDto 가져오기
            MemberDto memberDto = memberService.findMemberDtoByEmail(currentUserName);
            log.debug("memberDto: {}", memberDto);
            model.addAttribute("memberDto", memberDto);

            // ProfileDto 가져오기
            ProfileDto profileDto = memberService.toProfileDto(currentUserName);
            model.addAttribute("profileDto", profileDto);

            // AccountDto 가져오기
            AccountDto accountDto = accountService.findAccountDtoByMemberId(memberDto.getId());
            model.addAttribute("accountDto", accountDto);

            // 캐시와 결제 내역 조회
            List<TransactionDto> transactionHistory = transactionService.getTransactionHistory(memberDto.getId(), accountDto.getId());
            log.debug("transactionHistory: {}", transactionHistory);
            model.addAttribute("transactionHistory", transactionHistory);

            // 경매 참여 내역 조회
            // 입찰 중 내역
            List<Product> activeBids = bidService.getActiveBids(memberDto.getId());
            model.addAttribute("activeBids", activeBids);

            // 낙찰 내역
            List<Product> winningBids = bidService.getWinningBids(memberDto.getId());
            model.addAttribute("winningBids", winningBids);

            // 패찰 내역
            List<Product> failedBids = bidService.getFailedBids(memberDto.getId());
            model.addAttribute("failedBids", failedBids);

            // 경매 등록 내역 조회 - 각 조건 별로 데이터 가져오기
            List<Product> activeProducts = productService.getActiveProducts(memberDto.getId()); // 판매 중
            List<Product> completedProducts = productService.getCompletedProducts(memberDto.getId()); // 거래 완료
            List<Product> failedProducts = productService.getFailedProducts(memberDto.getId()); // 유찰

            model.addAttribute("activeProducts", activeProducts);
            model.addAttribute("completedProducts", completedProducts);
            model.addAttribute("failedProducts", failedProducts);

        } catch (UserNotFoundException e) {
            log.error("Member not found: {}", e.getMessage());
            return "error/404";
        }

        return "mypage/mypage";
    }
    
    // 프로필 수정
    @GetMapping("/profile-update")
    public String updateProfile(Model model) {
        log.info("GET /profile-update");

        // 로그인된 사용자의 정보를 가져오기 위해 SecurityContextHolder 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        ProfileDto profileDto = memberService.toProfileDto(currentUserName);
        log.debug("profileDto: {}", profileDto);
        model.addAttribute("profileDto", profileDto);

        return "mypage/profile-update";
    }

    // 프로필 수정
    @PostMapping("/profile-update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam(value="isRemovedImage") boolean isRemovedImage) {
        log.info("POST /profile-update");
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

    // 회원정보 수정
    @GetMapping("/userInfo-edit")
    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
