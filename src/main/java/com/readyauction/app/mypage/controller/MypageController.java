package com.readyauction.app.mypage.controller;

import com.readyauction.app.user.entity.User;
import com.readyauction.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    @Autowired
    private UserService userService;

    // 마이페이지
    @GetMapping("")
    public String mypage(Model model, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            // 기본 사용자 정보 또는 오류 처리
            model.addAttribute("error", "User ID is required.");
            return "error/404"; // 404 오류 페이지로 이동
        }

        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        return "mypage/mypage";
    }

    // 회원정보 수정
    @GetMapping("/profile-edit")
    public String editProfile() {
        return "mypage/profile-edit";
    }

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
