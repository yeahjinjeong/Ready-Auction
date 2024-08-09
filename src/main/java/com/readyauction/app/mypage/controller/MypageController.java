package com.readyauction.app.mypage.controller;

import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.user.entity.Member;
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
        String currentUserName = authentication.getName();
        System.out.println("currentUserName : " + currentUserName);

        Member member = memberService.findMemberByEmail(currentUserName);
//        Account account = accountService.findMemberByEmail(currentUserName);
        log.debug("member: {}", member);
        model.addAttribute("member", member);

        return "mypage/mypage";
    }
    
    // 프로필 수정
    @GetMapping("/profile-update")
    public String editProfile(Model model) {
        log.info("GET /profile-update");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        System.out.println("cuurentUserName : " + currentUserName);

        Member member = memberService.findMemberByEmail(currentUserName);
        log.debug("member: {}", member);
        model.addAttribute("member", member);

        return "mypage/profile-update";
    }

    // 프로필 수정
    @PostMapping("/profile-update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam(value="removeImage", required = false) String removeImage,
                                Model model) throws IOException {
        log.info("POST /profile-update");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Member member = memberService.findMemberByEmail(currentUserName);
        member.setNickname(nickname);

        boolean removeImageFlag = "true".equalsIgnoreCase(removeImage);

        if (removeImageFlag && member.getProfilePicture() != null) {
            memberService.deleteImage(member.getProfilePicture());
            member.setProfilePicture(null);
        }

        if (!image.isEmpty()) {
            String imageUrl = memberService.uploadImage(image, currentUserName);
            member.setProfilePicture(imageUrl);
        }

        memberService.save(member);
        log.debug("member: {}", member);
        model.addAttribute("member", member);

        return "redirect:/mypage";
    }

    // 회원정보 수정
    @GetMapping("/userInfo-edit")
    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
