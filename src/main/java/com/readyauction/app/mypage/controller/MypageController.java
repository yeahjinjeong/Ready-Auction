package com.readyauction.app.mypage.controller;

import com.readyauction.app.user.dto.MemberUpdateRequestDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/mypage")
@Slf4j
@RequiredArgsConstructor
public class MypageController {

    @Autowired
    private MemberService memberService;

    // 마이페이지
    @GetMapping("")
    public String getMyPage(Model model) {
        log.info("GET /mypage");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Member member = memberService.findMemberByEmail(currentUserName);
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
        System.out.println(currentUserName);

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

        if ("true".equalsIgnoreCase(removeImage)) {
            memberService.deleteImage(member.getProfilePicture());
            member.setProfilePicture(null);
        } else if (!image.isEmpty()) {
            String imageUrl = memberService.uploadImage(image, currentUserName);
            member.setProfilePicture(imageUrl);
        }

        memberService.save(member);
        log.debug("member: {}", member);
        model.addAttribute("member", member);

        return "redirect:/mypage";
    }

    // 닉네임 중복 검사
//    @PostMapping("/check-nickname")
//    @ResponseBody
//    public Map<String, Object> checkNickname(@RequestBody Map<String, String> request) {
//        String nickname = request.get("nickname");
//        boolean isAvailable = mypageService.isNicknameAvailable(nickname); // 닉네임 중복 확인 로직
//
//        Map<String, Object> response = new HashMap<>();
//        if (isAvailable) {
//            response.put("success", true);
//            response.put("email", mypageService.getCurrentUserEmail()); // 현재 사용자의 이메일
//        } else {
//            response.put("success", false);
//        }
//        return response;
//    }
    
    // 캐쉬 충전
    @GetMapping("/charge")
    public String chargeCash() {
        return "mypage/charge";
    }

    // 회원정보 수정
    @GetMapping("/userInfo-edit")
    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
