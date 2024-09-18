package com.readyauction.app.inquiry.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.common.paging.PageCriteria;
import com.readyauction.app.inquiry.dto.InquiryAnswerDto;
import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.service.AdminInquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/inquiry")
@Slf4j
@RequiredArgsConstructor
public class AdminInquiryController {
    private final AdminInquiryService adminInquiryService;

    // 관리자 문의 조회
    @GetMapping("/list")
    public String adminInquiry(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            Model model) {

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<InquiryDto> inquiryDtos = adminInquiryService.findAll(pageable);

        int page = inquiryDtos.getNumber();
        int limit = inquiryDtos.getTotalPages();
        int totalCount = (int) inquiryDtos.getTotalElements();
        String url = "list"; // 상대주소

        model.addAttribute("inquiryList", inquiryDtos);
        model.addAttribute("pageCriteria", new PageCriteria(page, limit, totalCount, url));

        log.info("inquiryDtos : {}", inquiryDtos);
        return "inquiry/admin-faq";
    }

    // 관리자 문의 상세 조회하기
    @GetMapping("/detail/{id}")
    public String adminInquiryDetail(
            @PathVariable Long id,
            Model model) {
        InquiryDetailDto inquiryDetailDto = adminInquiryService.findAndNicknameById(id);
        model.addAttribute("inquiry", inquiryDetailDto);
        log.info("inquiryDetailDto : {}", inquiryDetailDto);
        return "inquiry/admin-faq-detail";
    }

    // 관리자 댓글 추가하기
    @PostMapping("/detail/answer")
    public String createInquiryDetailAnswer(
            @ModelAttribute InquiryAnswerDto inquiryAnswerDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        log.info("inquiryAnswerDto : {}", inquiryAnswerDto);
        adminInquiryService.addAnswer(principal.getMember().getId(), inquiryAnswerDto);
        return "redirect:/admin/inquiry/detail/" + inquiryAnswerDto.getInquiryId();
    }

    // 관리자 댓글 수정하기
    @ResponseBody
    @PatchMapping("/detail/answer")
    public ResponseEntity<?> updateInquiryDetailAnswer(
            @RequestBody InquiryAnswerDto inquiryAnswerDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        log.info("inquiryAnswerDto : {}", inquiryAnswerDto);
        adminInquiryService.changeAnswer(principal.getMember().getId(), inquiryAnswerDto);
        return ResponseEntity.ok("ok");
    }

    // 댓글 삭제하기 -> 문의 엔티티 수정이라서 patch 사용
    @ResponseBody
    @PatchMapping("/detail/delete/answer")
    public ResponseEntity<?> deleteInquiryDetailAnswer(
            @RequestBody InquiryAnswerDto inquiryAnswerDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ){
        log.info("inquiryAnswerDto : {}", inquiryAnswerDto);
        adminInquiryService.deleteAnswer(principal.getMember().getId(), inquiryAnswerDto);
        return ResponseEntity.ok("ok");
    }

}
