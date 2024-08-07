package com.readyauction.app.mypage.controller;

import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage")
@Slf4j
@RequiredArgsConstructor
public class MypageController {

    @Autowired
    private MemberService memberService;

    // 마이페이지 - 파라미터
    @GetMapping("")
    public String mypage(Model model, @RequestParam(required = false) String email) {
        log.info("GET /mypage");

        if (email == null) {
            // 기본 사용자 정보 또는 오류 처리
            model.addAttribute("error", "User Email is required.");
            return "error/404"; // 404 오류 페이지로 이동
        }
//
//        Member member = memberService.findMemberByEmail(email);
//        log.debug("member: {}", member);
//        model.addAttribute("member", member);
//        return "mypage/mypage";
//    }

    // 마이페이지 - 세션
//    @GetMapping("")
//    public String myPage(HttpSession session, Model model) {
//        // 세션에서 이메일 가져오기
//        String email = (String) session.getAttribute("email");
//        if (email == null) {
//            // 로그인이 안된 상태면 로그인 페이지로 리다이렉트
//            return "redirect:/login";
//        }
//
//        // 이메일로 사용자 정보 조회
//        Member member = mypageService.findMemberByEmail(email);
//        String processedAddress = mypageService.extractAddressPart(member.getAddress());
//
//        // 모델에 사용자 정보 추가
//        model.addAttribute("member", member);
//        model.addAttribute("processedAddress", processedAddress);
//        return "mypage/mypage";
//    }

//    // 프로필 수정
//    @GetMapping("/profile-update")
//    public void updateProfile(/*HttpSession session,*/ Model model) {
//        log.info("GET /mypage/profile-update");
//        String email = "ssg@gmail.com";
////        String email = (String) session.getAttribute("email");
////        if (email == null) {
////            return "redirect:/login";
////        }
//        Member member = memberService.findMemberByEmail(email);
//        log.debug("member: {}", member);
//        model.addAttribute("member", member);
//    }
//
//    // 프로필 수정
//    @PostMapping("/profile-update")
//    public String updateProfile(@ModelAttribute MemberUpdateRequestDto dto, RedirectAttributes redirectAttributes) {
//        log.info("POST /mypage/profile-update");
//        memberService.updateProfile(dto);
//        redirectAttributes.addFlashAttribute("message", "프로필을 수정했습니다.");
////        return "redirect:/mypage/mypage?email=" + dto.getEmail();
//        return "redirect:/mypage?email=" + "ssg@gmail.com";
//    }
//
//    // 닉네임 중복 검사
////    @PostMapping("/check-nickname")
////    @ResponseBody
////    public Map<String, Object> checkNickname(@RequestBody Map<String, String> request) {
////        String nickname = request.get("nickname");
////        boolean isAvailable = mypageService.isNicknameAvailable(nickname); // 닉네임 중복 확인 로직
////
////        Map<String, Object> response = new HashMap<>();
////        if (isAvailable) {
////            response.put("success", true);
////            response.put("email", mypageService.getCurrentUserEmail()); // 현재 사용자의 이메일
////        } else {
////            response.put("success", false);
////        }
////        return response;
////    }
//
//    // 캐쉬 충전
//    @GetMapping("/charge")
//    public String chargeCash() {
//        return "mypage/charge";
//    }
//
//    // 회원정보 수정
//    @GetMapping("/userInfo-edit")
//    public String editUserInfo() {
        return "mypage/userInfo-edit";
    }
}
