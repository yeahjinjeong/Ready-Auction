package com.readyauction.app.mypage.controller;

import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.ProfileDto;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MemberService memberService;

    @Autowired
    private AccountService accountService;

    // 마이페이지
    @GetMapping("")
    public String getMyPage(Model model) {
        log.info("GET /mypage");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 로그인한 이메일
        System.out.println("currentUserName : " + currentUserName);

        try {
            MemberDto memberDto = memberService.findMemberDtoByEmail(currentUserName);
            ProfileDto profileDto = memberService.toProfileDto(currentUserName);
            log.debug("memberDto: {}", memberDto);
            log.debug("profileDto: {}", profileDto);
            model.addAttribute("memberDto", memberDto);
            model.addAttribute("profileDto", profileDto);
        } catch (UserNotFoundException e) {
            log.error("Member not found: {}", e.getMessage());
//            return "error/404";
        }

        return "mypage/mypage";
    }
    
    // 프로필 수정
    @GetMapping("/profile-update")
    public String updateProfile(Model model) {
        log.info("GET /profile-update");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        System.out.println("currentUserName : " + currentUserName);

        ProfileDto profileDto = memberService.toProfileDto(currentUserName);
        log.debug("profileDto: {}", profileDto);
        model.addAttribute("profileDto", profileDto);

        return "mypage/profile-update";
    }

    @PostMapping("/profile-update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam(value="removeImage", required = false) String removeImage,
                                Model model) throws IOException {
        log.info("POST /profile-update");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        try {
            memberService.updateProfile(currentUserName, nickname, image, removeImage);
        } catch (IOException e) {
            log.error("Error updating profile", e);
        }

        ProfileDto profileDto = memberService.toProfileDto(currentUserName);
        log.debug("ProfileDto: {}", profileDto);
        model.addAttribute("profileDto", profileDto);

        return "redirect:/mypage";
    }

    // 회원정보 수정
    @GetMapping("/userInfo-edit")
    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
