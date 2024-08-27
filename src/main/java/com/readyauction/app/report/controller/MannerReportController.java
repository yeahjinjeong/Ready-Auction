package com.readyauction.app.report.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.report.dto.MannerReportDto;
import com.readyauction.app.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/report/manner")
public class MannerReportController {
    private final ReportService reportService;

    @GetMapping("/{memberId}")
    public String mannerFrm(
            @PathVariable Long memberId,
            Model model
    ) {
        model.addAttribute("memberId", memberId);
        return "report/manner-form";
    }

    @PostMapping("/register")
    public String register(
//            @RequestParam Map<String, String> allParams,
//            @RequestParam("likes") List<Like> likes,
            @ModelAttribute MannerReportDto mannerReportDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
//        log.info("allParams : {}", allParams);
        log.info("mannerReportDto : {}", mannerReportDto);
//        log.info("likes : {}", likes);
        // 매너폼을 저장하기
        // + 매너스코어 반영하기 => 멤버 엔티티 조회 후 change 하기~!
        reportService.applyMannerScore(principal.getMember().getId(), mannerReportDto);
        return "redirect:/chat/list";
    }
}
