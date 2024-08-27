package com.readyauction.app.inquiry.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.inquiry.dto.InquiryAnswerDto;
import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.dto.InquiryReqDto;
import com.readyauction.app.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inquiry")
@Slf4j
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @GetMapping("/faq")
    public void inquiry() {

    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("inquiryReqDto", new InquiryReqDto());
        return "inquiry/inquiry-register";
    }

    // 문의 등록하기
    @PostMapping("/register")
    public String registerInquiry(@ModelAttribute InquiryReqDto inquiryReqDto) {
        inquiryService.registerInquiry(inquiryReqDto);
        return "redirect:/mypage";
    }
    // 문의 게시글 조회
    @GetMapping("/{id}")
    public String getInquiryDetail(@PathVariable("id") Long id, Model model) {
        InquiryDetailDto inquiryDetailDto = inquiryService.findAndNicknameById(id);
        log.debug("inquiryDetailDto : {}", inquiryDetailDto);
        model.addAttribute("inquiry", inquiryDetailDto);
        return "inquiry/inquiry-detail";
    }

}
