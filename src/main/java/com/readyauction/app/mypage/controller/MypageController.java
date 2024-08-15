package com.readyauction.app.mypage.controller;

import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.service.AccountService;
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

@Controller
@RequestMapping("/mypage")
@Slf4j
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final AccountService accountService;

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
            log.debug("profileDto: {}", profileDto);
            model.addAttribute("profileDto", profileDto);

            // AccountDto 가져오기
            AccountDto accountDto = accountService.findAccountDtoByMemberId(memberDto.getId());
            log.debug("accountDto: {}", accountDto);
            model.addAttribute("accountDto", accountDto);

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
                                @RequestParam(value="removeImage", required = false) String removeImage) {
        log.info("POST /profile-update");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            memberService.updateProfile(currentUserName, nickname, image, removeImage);
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
